package frontend.auction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import frontend.utils.BackendClient;
import frontend.utils.SessionManager;
import frontend.utils.WebSocketClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

/**
 * Controller cho màn hình chi tiết phiên đấu giá.
 * Bao gồm: thông tin auction, đặt giá, auto-bid, biểu đồ giá (LineChart).
 * Nhận realtime updates qua WebSocketClient (polling).
 */
public class AuctionDetailController {

    @FXML private Label titleLabel;
    @FXML private Label categoryLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label currentPriceLabel;
    @FXML private Label bidCountLabel;
    @FXML private Label statusLabel;
    @FXML private Label endTimeLabel;
    @FXML private Label timerLabel;
    @FXML private Label winnerLabel;
    @FXML private Label userStatusLabel;
    @FXML private Label messageLabel;

    @FXML private TextField bidAmountField;
    @FXML private Button placeBidButton;

    @FXML private TextField maxBidField;
    @FXML private TextField incrementField;
    @FXML private Button autoBidButton;

    @FXML private LineChart<Number, Number> priceChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    @FXML private ListView<String> bidHistoryList;

    private Long auctionId;
    private final ObjectMapper mapper = new ObjectMapper();
    private XYChart.Series<Number, Number> priceSeries;
    private int chartPointIndex = 0;
    private Timeline countdownTimeline;
    private LocalDateTime auctionEndTime;
    private boolean isFinishedAlertShown = false;

    /**
     * Được gọi trước khi hiển thị màn hình, truyền auction ID.
     */
    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
        loadAuctionDetail();
        loadBidHistory();
        startRealtimeUpdates();
    }

    @FXML
    public void initialize() {
        // Setup chart
        priceSeries = new XYChart.Series<>();
        priceSeries.setName("Gia dau gia");
        priceChart.getData().add(priceSeries);
        priceChart.setCreateSymbols(true);
        priceChart.setAnimated(false);

        // Chỉ cho bidder đặt giá
        boolean canBid = SessionManager.getInstance().isBidder();
        placeBidButton.setDisable(!canBid);
        autoBidButton.setDisable(!canBid);

        if (!canBid) {
            userStatusLabel.setText("Ban dang dang nhap voi quyen SELLER/ADMIN. Chi Bidder moi co the dat gia.");
            userStatusLabel.getStyleClass().add("status-not-bidder");
        }

        setupCountdownTimer();
    }

    private void setupCountdownTimer() {
        countdownTimeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), event -> {
            if (auctionEndTime != null) {
                updateCountdownDisplay();
            }
        }));
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
    }

    private void updateCountdownDisplay() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(auctionEndTime)) {
            timerLabel.setText("00:00:00");
            timerLabel.setStyle("-fx-text-fill: gray; -fx-background-color: #e5e7eb;");
            return;
        }

        long totalSeconds = ChronoUnit.SECONDS.between(now, auctionEndTime);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

        // Nêu còn dưới 1 phút thì đổi màu đỏ đậm
        if (totalSeconds < 60) {
            timerLabel.setStyle("-fx-text-fill: white; -fx-background-color: #dc2626;");
        } else {
            timerLabel.setStyle(""); // Reset to CSS
        }
    }

    /**
     * Tải thông tin chi tiết auction từ server.
     */
    private void loadAuctionDetail() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().get("/auctions/" + auctionId);
                if (response.statusCode() == 200) {
                    JsonNode node = mapper.readTree(response.body());
                    Platform.runLater(() -> updateUI(node));
                }
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText("Loi tai du lieu: " + e.getMessage()));
            }
        }).start();
    }

    /**
     * Tải lịch sử bid và cập nhật biểu đồ.
     */
    private void loadBidHistory() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().get("/auctions/" + auctionId + "/bids");
                if (response.statusCode() == 200) {
                    JsonNode bids = mapper.readTree(response.body());
                    Platform.runLater(() -> {
                        ObservableList<String> items = FXCollections.observableArrayList();
                        priceSeries.getData().clear();
                        chartPointIndex = 0;

                        // Duyệt từ cuối lên đầu (vì API trả về desc)
                        for (int i = bids.size() - 1; i >= 0; i--) {
                            JsonNode bid = bids.get(i);
                            double amount = bid.path("amount").asDouble();
                            String bidder = bid.path("bidder").path("username").asText("?");
                            String time = bid.path("bidTime").asText("");

                            items.add(0, String.format("%s - %,.0f VND boi %s", formatTime(time), amount, bidder));
                            priceSeries.getData().add(new XYChart.Data<>(chartPointIndex++, amount));
                        }
                        bidHistoryList.setItems(items);
                    });
                }
            } catch (Exception e) {
                // ignore
            }
        }).start();
    }

    /**
     * Bắt đầu nhận realtime updates.
     */
    private void startRealtimeUpdates() {
        Consumer<String> listener = data -> {
            try {
                JsonNode node = mapper.readTree(data);
                updateUI(node);
                loadBidHistory(); // Refresh bid history & chart
            } catch (Exception ignored) {}
        };

        WebSocketClient.getInstance().subscribe("/topic/auctions/" + auctionId, listener);
        WebSocketClient.getInstance().startPolling(auctionId);
    }

    /**
     * Cập nhật giao diện từ dữ liệu JSON.
     */
    private void updateUI(JsonNode node) {
        titleLabel.setText(node.path("title").asText(""));
        categoryLabel.setText("Loai: " + node.path("category").asText(""));
        descriptionLabel.setText(node.path("description").asText(""));

        BigDecimal price = new BigDecimal(node.path("currentPrice").asText("0"));
        currentPriceLabel.setText(String.format("%,.0f VND", price));
        bidCountLabel.setText(node.path("bidCount").asInt() + " luot dat gia");

        String status = node.path("status").asText("UNKNOWN");
        statusLabel.setText(status);
        statusLabel.setStyle(getStatusStyle(status));

        String endTimeStr = node.path("endTime").asText("");
        endTimeLabel.setText("Ket thuc: " + formatTime(endTimeStr));
        try {
            auctionEndTime = LocalDateTime.parse(endTimeStr);
        } catch (Exception ignored) {}

        JsonNode winner = node.path("winner");
        Long currentUserId = SessionManager.getInstance().getUserId();
        boolean isBidder = SessionManager.getInstance().isBidder();

        if (winner != null && !winner.isMissingNode() && !winner.isNull()) {
            Long winnerId = winner.path("id").asLong();
            String winnerName = winner.path("username").asText("");
            winnerLabel.setText("Nguoi dan dau: " + winnerName);

            if (isBidder) {
                userStatusLabel.getStyleClass().removeAll("status-leading", "status-outbid", "status-not-bidder");
                if (currentUserId != null && currentUserId.equals(winnerId)) {
                    userStatusLabel.setText("★ BAN DANG DAN DAU!");
                    userStatusLabel.getStyleClass().add("status-leading");
                } else {
                    userStatusLabel.setText("⚠ BAN DA BI VUOT MAT! Hay dat gia cao hon!");
                    userStatusLabel.getStyleClass().add("status-outbid");
                }
            }
        } else {
            winnerLabel.setText("Chua co nguoi dat gia");
            if (isBidder) {
                userStatusLabel.setText("Hay la nguoi dau tien dat gia!");
                userStatusLabel.getStyleClass().removeAll("status-leading", "status-outbid");
            }
        }

        // Disable bidding nếu auction không đang chạy
        boolean isRunning = "RUNNING".equals(status);
        placeBidButton.setDisable(!isRunning || !isBidder);
        autoBidButton.setDisable(!isRunning || !isBidder);

        if ("FINISHED".equals(status) && !isFinishedAlertShown) {
            isFinishedAlertShown = true;
            Platform.runLater(() -> {
                String msg = (winner != null && !winner.isNull()) 
                    ? "Phien dau gia ket thuc! Nguoi thang: " + winner.path("username").asText()
                    : "Phien dau gia ket thuc ma khong co nguoi mua.";
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thong bao ket thuc");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.show();
            });
        }
    }

    // ==================== Actions ====================

    @FXML
    private void onPlaceBid() {
        try {
            String amountText = bidAmountField.getText().trim();
            if (amountText.isEmpty()) {
                messageLabel.setText("Vui long nhap so tien!");
                return;
            }

            BigDecimal amount = new BigDecimal(amountText);
            Long userId = SessionManager.getInstance().getUserId();

            placeBidButton.setDisable(true);
            messageLabel.setText("Dang dat gia...");

            new Thread(() -> {
                try {
                    JSONObject body = new JSONObject();
                    body.put("bidderId", userId);
                    body.put("amount", amount);

                    HttpResponse<String> response = BackendClient.getInstance()
                            .post("/auctions/" + auctionId + "/bid", body.toString());

                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            messageLabel.setText("Dat gia thanh cong!");
                            messageLabel.setStyle("-fx-text-fill: #22c55e;");
                            bidAmountField.clear();
                            loadAuctionDetail();
                            loadBidHistory();
                        } else {
                            messageLabel.setText("Loi: " + response.body());
                            messageLabel.setStyle("-fx-text-fill: #ef4444;");
                        }
                        placeBidButton.setDisable(false);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        messageLabel.setText("Loi ket noi: " + e.getMessage());
                        messageLabel.setStyle("-fx-text-fill: #ef4444;");
                        placeBidButton.setDisable(false);
                    });
                }
            }).start();
        } catch (NumberFormatException e) {
            messageLabel.setText("So tien khong hop le!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    @FXML
    private void onAutoBid() {
        try {
            String maxBidText = maxBidField.getText().trim();
            String incrementText = incrementField.getText().trim();

            if (maxBidText.isEmpty() || incrementText.isEmpty()) {
                messageLabel.setText("Vui long nhap max bid va buoc gia!");
                return;
            }

            BigDecimal maxBid = new BigDecimal(maxBidText);
            BigDecimal increment = new BigDecimal(incrementText);
            Long userId = SessionManager.getInstance().getUserId();

            autoBidButton.setDisable(true);
            messageLabel.setText("Dang dang ky auto-bid...");

            new Thread(() -> {
                try {
                    JSONObject body = new JSONObject();
                    body.put("bidderId", userId);
                    body.put("maxBid", maxBid);
                    body.put("increment", increment);

                    HttpResponse<String> response = BackendClient.getInstance()
                            .post("/auctions/" + auctionId + "/auto-bid", body.toString());

                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            messageLabel.setText("Dang ky auto-bid thanh cong!");
                            messageLabel.setStyle("-fx-text-fill: #22c55e;");
                            maxBidField.clear();
                            incrementField.clear();
                        } else {
                            messageLabel.setText("Loi: " + response.body());
                            messageLabel.setStyle("-fx-text-fill: #ef4444;");
                        }
                        autoBidButton.setDisable(false);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        messageLabel.setText("Loi ket noi: " + e.getMessage());
                        messageLabel.setStyle("-fx-text-fill: #ef4444;");
                        autoBidButton.setDisable(false);
                    });
                }
            }).start();
        } catch (NumberFormatException e) {
            messageLabel.setText("So tien khong hop le!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    @FXML
    private void onBack() {
        if (countdownTimeline != null) countdownTimeline.stop();
        WebSocketClient.getInstance().stopPolling();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            Scene scene = new Scene(root, 1180, 760);
            scene.getStylesheets().add(getClass().getResource("/style/dashboard.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Auction Web - Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== Helpers ====================

    private String formatTime(String isoTime) {
        if (isoTime == null || isoTime.isBlank()) return "-";
        try {
            LocalDateTime dt = LocalDateTime.parse(isoTime);
            return dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        } catch (Exception e) {
            return isoTime;
        }
    }

    private String getStatusStyle(String status) {
        return switch (status) {
            case "RUNNING" -> "-fx-text-fill: #22c55e; -fx-font-weight: bold;";
            case "FINISHED" -> "-fx-text-fill: #ef4444; -fx-font-weight: bold;";
            case "OPEN" -> "-fx-text-fill: #3b82f6; -fx-font-weight: bold;";
            default -> "-fx-text-fill: #64748b;";
        };
    }
}
