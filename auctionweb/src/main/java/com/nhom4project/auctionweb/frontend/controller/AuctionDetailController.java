package com.nhom4project.auctionweb.frontend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom4project.auctionweb.frontend.utils.BackendClient;
import com.nhom4project.auctionweb.frontend.utils.SceneUtils;
import com.nhom4project.auctionweb.frontend.utils.SessionManager;
import com.nhom4project.auctionweb.frontend.utils.WebSocketClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
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

    @FXML
    private Label titleLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label currentPriceLabel;
    @FXML
    private Label bidCountLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label endTimeLabel;
    @FXML
    private Label timerLabel;
    @FXML
    private Label winnerLabel;
    @FXML
    private Label userStatusLabel;
    @FXML
    private Label messageLabel;

    @FXML
    private TextField bidAmountField;
    @FXML
    private Button placeBidButton;

    @FXML
    private TextField maxBidField;
    @FXML
    private TextField incrementField;
    @FXML
    private Button autoBidButton;
    @FXML
    private Button stopAutoBidButton;
    @FXML
    private Label minBidLabel;

    @FXML
    private VBox managementCard;
    @FXML
    private Button startAuctionButton;
    @FXML
    private Button endEarlyButton;
    @FXML
    private Button deleteAuctionButton;

    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private CategoryAxis lineXAxis;
    @FXML
    private NumberAxis lineYAxis;

    @FXML
    private ListView<String> bidHistoryList;

    // Lưu ID của phiên đấu giá đang xem
    private Long auctionId;
    // Công cụ để chuyển đổi chuỗi JSON từ Server gửi về thành dạng dữ liệu phân
    // tích được
    private final ObjectMapper mapper = new ObjectMapper();
    // Chứa các điểm dữ liệu để vẽ đường biểu diễn giá trên biểu đồ
    private XYChart.Series<String, Number> lineSeries;
    private int chartPointIndex = 0;
    // Bộ đếm thời gian lùi (đếm ngược)
    private Timeline countdownTimeline;
    // Thời gian kết thúc phiên đấu
    private LocalDateTime auctionEndTime;
    private LocalDateTime auctionStartTime;
    private String auctionStatus;
    private boolean isFinishedAlertShown = false;
    private BigDecimal startingPrice;

    /**
     * Được gọi trước khi hiển thị màn hình, từ màn hình Dashboard khi click vào 1
     * phiên đấu giá.
     * Nhận ID và kích hoạt việc tải dữ liệu.
     */
    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
        loadAuctionDetail(); // Gọi API lấy thông tin phiên đấu giá
        loadBidHistory(); // Gọi API lấy lịch sử đặt giá
        startRealtimeUpdates(); // Bật kết nối WebSocket để nhận giá mới theo thời gian thực
    }

    /**
     * Hàm initialize() luôn tự động chạy khi màn hình FXML vừa load xong.
     */
    @FXML
    public void initialize() {
        // Khởi tạo Series cho LineChart
        lineSeries = new XYChart.Series<>();
        lineSeries.setName("Giá");
        if (lineChart != null) {
            lineChart.getData().add(lineSeries);
            lineChart.setAnimated(false);
            lineChart.setCreateSymbols(true);
            // Bật grid ngang dạng dashed như ảnh mẫu
            lineChart.setHorizontalGridLinesVisible(true);
            lineChart.setVerticalGridLinesVisible(false);
        }

        // Cấu hình định dạng và hiển thị trục
        if (lineYAxis != null) {
            lineYAxis.setForceZeroInRange(false);
            lineYAxis.setAutoRanging(true);
            lineYAxis.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
                @Override
                public String toString(Number object) {
                    return String.format("%,.0f", object.doubleValue());
                }

                @Override
                public Number fromString(String string) {
                    return Double.parseDouble(string);
                }
            });
        }

        if (lineXAxis != null) {
            lineXAxis.setTickLabelRotation(0);
        }

        // Kiểm tra quyền người dùng thông qua SessionManager.
        // Chỉ cho bidder đặt giá, nếu không phải là người mua (Bidder) thì sẽ vô hiệu
        // hóa các nút Đặt giá.
        boolean canBid = SessionManager.getInstance().isBidder();
        placeBidButton.setDisable(!canBid);
        autoBidButton.setDisable(!canBid);

        if (!canBid) {
            userStatusLabel.setText("Bạn đang đăng nhập với quyền SELLER. Chỉ Bidder mới có thể đặt giá.");
            userStatusLabel.getStyleClass().add("status-not-bidder");
        }

        // Gọi hàm bắt đầu đếm ngược
        setupCountdownTimer();
    }

    /**
     * Bộ đếm ngược thời gian: Tạo một Timeline chạy lặp lại mỗi 1 giây:duration
     */
    private void setupCountdownTimer() {
        countdownTimeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), event -> {
            if (auctionEndTime != null) {
                updateCountdownDisplay(); // Mỗi giây trôi qua, cập nhật giao diện hiển thị giờ
            }
        }));
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);// vòng lặp lặp đi lặp lại mãi
        countdownTimeline.play();
    }

    /**
     * Tính toán khoảng thời gian giữa hiện tại và lúc kết thúc.
     * Đổi giây ra định dạng Giờ:Phút:Giây (%02d:%02d:%02d).
     */
    private void updateCountdownDisplay() {
        long totalSeconds;
        if ("OPEN".equalsIgnoreCase(auctionStatus)) {
            if (auctionStartTime != null && auctionEndTime != null) {
                totalSeconds = ChronoUnit.SECONDS.between(auctionStartTime, auctionEndTime);
            } else {
                totalSeconds = 0;
            }
            if (totalSeconds < 0) totalSeconds = 0;
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;
            timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            timerLabel.setStyle(""); // Reset to CSS (no danger colors)
            return;
        }

        if ("FINISHED".equalsIgnoreCase(auctionStatus)) {
            timerLabel.setText("00:00:00");
            timerLabel.setStyle("-fx-text-fill: gray; -fx-background-color: #e5e7eb;");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(auctionEndTime)) {
            timerLabel.setText("00:00:00");
            timerLabel.setStyle("-fx-text-fill: gray; -fx-background-color: #e5e7eb;");
            return;
        }

        totalSeconds = ChronoUnit.SECONDS.between(now, auctionEndTime);
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
     * Gọi API lấy dữ liệu tĩnh: Tạo một Thread (luồng) mới để gọi API GET
     * /auctions/{id}.
     * Trả về thông tin chi tiết (tên, giá hiện tại, trạng thái).
     */
    private void loadAuctionDetail() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().get("/auctions/" + auctionId);
                if (response.statusCode() == 200) {
                    JsonNode node = mapper.readTree(response.body());
                    // Cập nhật giao diện phải nằm trong Platform.runLater để tránh lỗi đa luồng
                    // JavaFX
                    Platform.runLater(() -> updateUI(node));
                }
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText("Loi tai du lieu: " + e.getMessage()));
            }
        }).start();
    }

    /**
     * Tạo một luồng mới gọi API GET /auctions/{id}/bids để lấy toàn bộ lịch sử đặt
     * giá.
     * Cập nhật danh sách lịch sử và biểu đồ giá.
     */
    private void loadBidHistory() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().get("/auctions/" + auctionId + "/bids");
                if (response.statusCode() == 200) {
                    JsonNode bids = mapper.readTree(response.body());
                    // Cập nhật List và Chart phải nằm trong Platform.runLater
                    Platform.runLater(() -> {
                        ObservableList<String> items = FXCollections.observableArrayList();
                        if (lineSeries != null)
                            lineSeries.getData().clear();
                        if (lineXAxis != null)
                            lineXAxis.getCategories().clear();

                        // Thêm giá khởi điểm làm điểm đầu tiên của biểu đồ
                        if (startingPrice != null) {
                            String category = "Khởi điểm";
                            double value = startingPrice.doubleValue();
                            XYChart.Data<String, Number> lineData = new XYChart.Data<>(category, value);
                            String tooltipText = String.format("Giá khởi điểm: %,.0f VND", value);
                            addCustomSymbolAndLabel(lineData, value, tooltipText, false);
                            if (lineSeries != null)
                                lineSeries.getData().add(lineData);
                        }

                        // Duyệt từ cuối lên đầu (vì API trả về desc) để đảo ngược chiều ưu tiên
                        int bidIndex = 1;
                        for (int i = bids.size() - 1; i >= 0; i--) {
                            JsonNode bid = bids.get(i);
                            double amount = bid.path("amount").asDouble();
                            String bidderUsername = bid.path("bidder").path("username").asText("?");
                            String bidderFullname = bid.path("bidder").path("fullname").asText("");
                            String bidder = (bidderFullname != null && !bidderFullname.isBlank()) ? bidderFullname
                                    : bidderUsername;
                            String time = bid.path("bidTime").asText("");

                            items.add(0, String.format("%s - %,.0f VND boi %s", formatTime(time), amount, bidder));

                            String category = "Lượt " + (bidIndex++);
                            XYChart.Data<String, Number> lineData = new XYChart.Data<>(category, amount);
                            String tooltipText = String.format("%s\nGiá: %,.0f VND\nNgười đặt: %s\nThời gian: %s",
                                    category, amount, bidder, formatTime(time));
                            addCustomSymbolAndLabel(lineData, amount, tooltipText, true);
                            if (lineSeries != null)
                                lineSeries.getData().add(lineData);
                        }
                        bidHistoryList.setItems(items); // Cập nhật danh sách bidHistoryList
                    });
                }
            } catch (Exception e) {
                // ignore
            }
        }).start();
    }

    private void addCustomSymbolAndLabel(XYChart.Data<String, Number> data, double value, String tooltipText,
            boolean isBidderPoint) {
        Runnable customizeNode = () -> {
            Node node = data.getNode();
            if (node instanceof StackPane symbolNode) {
                if (symbolNode.getUserData() != null && "custom".equals(symbolNode.getUserData())) {
                    return;
                }
                symbolNode.setUserData("custom");

                Circle circle = new Circle(6);
                circle.setFill(Color.web("#1a1a1a"));
                circle.setStroke(Color.web("#1a1a1a"));
                circle.setStrokeWidth(1.0);

                // === Nhãn số tiền hiển thị phía trên đỉnh ===
                Label label = new Label(formatCompactValue(value));
                label.setStyle(
                        "-fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #1a1a1a;");
                label.setManaged(false);
                // Đặt nhãn lên trên circle (lên 22px)
                label.setTranslateY(-22);
                label.widthProperty().addListener((o, ov, nv) -> {
                    label.setTranslateX(-nv.doubleValue() / 2);
                });

                // Xóa nền mặc định của symbol để hiển thị chấm tròn tùy chỉnh
                symbolNode.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
                symbolNode.getChildren().clear();
                symbolNode.getChildren().addAll(circle, label);

                // === Vẽ đường dọc dashed từ điểm xuống trục X ===
                symbolNode.layoutYProperty().addListener((obs2, ov, nv) -> {
                    drawDropLine(symbolNode, data);
                });
                symbolNode.layoutXProperty().addListener((obs2, ov, nv) -> {
                    drawDropLine(symbolNode, data);
                });
                Platform.runLater(() -> drawDropLine(symbolNode, data));

                Tooltip tooltip = new Tooltip(tooltipText);
                tooltip.setShowDelay(javafx.util.Duration.millis(100));
                tooltip.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
                Tooltip.install(symbolNode, tooltip);
                symbolNode.setCursor(javafx.scene.Cursor.HAND);
            }
        };

        if (data.getNode() != null) {
            customizeNode.run();
        }

        data.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                customizeNode.run();
            }
        });
    }

    /**
     * Vẽ đường dọc dạng dashed từ điểm trên đồ thị xuống trục X, giống ảnh mẫu.
     */
    private void drawDropLine(StackPane container, XYChart.Data<String, Number> data) {
        // Xóa đường cũ nếu có (tag bằng userData "dropline")
        if (lineChart == null)
            return;
        Pane chartPlot = (Pane) lineChart.lookup(".chart-plot-background");
        if (chartPlot == null)
            return;

        // Tìm và xóa đường cũ cho điểm này
        String lineId = "dropline_" + System.identityHashCode(data);
        chartPlot.getChildren().removeIf(n -> lineId.equals(n.getUserData()));

        double nodeX = container.getLayoutX() + container.getWidth() / 2;
        double nodeY = container.getLayoutY() + container.getHeight() / 2;
        double plotHeight = chartPlot.getHeight();

        if (plotHeight <= 0 || nodeX <= 0)
            return;

        Line dropLine = new Line(nodeX, nodeY, nodeX, plotHeight);
        dropLine.setStroke(Color.web("#aaaaaa"));
        dropLine.setStrokeWidth(1.0);
        dropLine.getStrokeDashArray().addAll(5.0, 4.0);
        dropLine.setUserData(lineId);
        dropLine.setMouseTransparent(true);

        chartPlot.getChildren().add(0, dropLine); // Thêm ở dưới cùng để không che đường chính
    }

    private String formatCompactValue(double value) {
        if (value >= 1_000_000_000) {
            double bill = value / 1_000_000_000.0;
            return bill % 1 == 0 ? String.format("%.0fB", bill) : String.format("%.1fB", bill);
        } else if (value >= 1_000_000) {
            double mill = value / 1_000_000.0;
            return mill % 1 == 0 ? String.format("%.0fM", mill) : String.format("%.1fM", mill);
        } else if (value >= 1_000) {
            double k = value / 1_000.0;
            return k % 1 == 0 ? String.format("%.0fK", k) : String.format("%.1fK", k);
        } else {
            return String.format("%.1f", value);
        }
    }

    /**
     * Nhận dữ liệu Real-time: Bất cứ khi nào server có thông báo mới (ví dụ ai đó
     * vừa đặt giá),
     * hàm listener sẽ được kích hoạt. Ngay lập tức gọi lại hàm updateUI(node) và
     * loadBidHistory()
     * để cập nhật lại số tiền, trạng thái, và vẽ lại biểu đồ mà không cần tải lại
     * trang.
     */
    private void startRealtimeUpdates() {
        Consumer<String> listener = data -> {
            try {
                JsonNode node = mapper.readTree(data);
                updateUI(node); // Cập nhật các thông số UI cơ bản
                loadBidHistory(); // Cập nhật lại lịch sử đặt giá và biểu đồ
            } catch (Exception ignored) {
            }
        };

        // Đăng ký nhận thông điệp qua WebSocket
        WebSocketClient.getInstance().subscribe("/topic/auctions/" + auctionId, listener);
        WebSocketClient.getInstance().startPolling(auctionId);
    }

    /**
     * Cập nhật giao diện từ dữ liệu JSON.
     * Trích xuất các trường từ JSON (title, description, currentPrice...) và gán
     * văn bản cho Label tương ứng.
     */
    private void updateUI(JsonNode node) {
        titleLabel.setText(node.path("title").asText(""));
        categoryLabel.setText("Loai: " + node.path("category").asText(""));
        descriptionLabel.setText(node.path("description").asText(""));

        BigDecimal price = new BigDecimal(node.path("currentPrice").asText("0"));
        currentPriceLabel.setText(String.format("%,.0f VND", price));
        bidCountLabel.setText(node.path("bidCount").asInt() + " luot dat gia");

        startingPrice = new BigDecimal(node.path("startingPrice").asText("0"));

        String status = node.path("status").asText("UNKNOWN");
        statusLabel.setText(status);
        statusLabel.setStyle(getStatusStyle(status));
        auctionStatus = status;

        String startTimeStr = node.path("startTime").asText("");
        try {
            auctionStartTime = LocalDateTime.parse(startTimeStr);
        } catch (Exception ignored) {
        }

        String endTimeStr = node.path("endTime").asText("");
        endTimeLabel.setText("Ket thuc: " + formatTime(endTimeStr));
        try {
            auctionEndTime = LocalDateTime.parse(endTimeStr);
        } catch (Exception ignored) {
        }

        JsonNode winner = node.path("winner");
        JsonNode seller = node.path("seller");
        Long currentUserId = SessionManager.getInstance().getUserId();
        String currentRole = SessionManager.getInstance().getRole();
        boolean isBidder = SessionManager.getInstance().isBidder();
        boolean isAdmin = SessionManager.getInstance().isAdmin();
        boolean isOwner = seller != null && !seller.isNull()
                && seller.path("id").asLong() == (currentUserId != null ? currentUserId : -1);

        // Management visibility
        if (isAdmin || isOwner) {
            managementCard.setVisible(true);
            managementCard.setManaged(true);
            endEarlyButton.setVisible(isOwner);
            endEarlyButton.setManaged(isOwner);
            // Delete is for both Admin and Owner
            deleteAuctionButton.setVisible(true);
            deleteAuctionButton.setManaged(true);
            // Start is for Owner
            startAuctionButton.setVisible(isOwner);
            startAuctionButton.setManaged(isOwner);

            // Disable end early if already finished
            endEarlyButton.setDisable(!"RUNNING".equals(status));
            // Disable start if not OPEN
            startAuctionButton.setDisable(!"OPEN".equals(status));
        } else {
            managementCard.setVisible(false);
            managementCard.setManaged(false);
        }

        // Logic xét người dẫn đầu: Kiểm tra xem ID người chiến thắng trả về có trùng
        // với ID của tài khoản đang đăng nhập hay không.
        if (winner != null && !winner.isMissingNode() && !winner.isNull()) {
            Long winnerId = winner.path("id").asLong();
            String winnerFullname = winner.path("fullname").asText("");
            String winnerName = !winnerFullname.isBlank() ? winnerFullname : winner.path("username").asText("");
            winnerLabel.setText("Nguoi dan dau: " + winnerName);

            if (isBidder) {
                userStatusLabel.getStyleClass().removeAll("status-leading", "status-outbid", "status-not-bidder");
                if (currentUserId != null && currentUserId.equals(winnerId)) {
                    // Nếu trùng, báo "BẠN ĐANG DẪN ĐẦU" (màu xanh)
                    userStatusLabel.setText("★ BẠN ĐANG DẪN ĐẦU!");
                    userStatusLabel.getStyleClass().add("status-leading");
                } else {
                    // Nếu không trùng thì báo "BẠN ĐẠ BỊ VƯỢT MẶT" (màu đỏ)
                    userStatusLabel.setText("⚠ BẠN ĐÃ BỊ VƯỢT MẶT! Hãy đặt giá cao hơn!");
                    userStatusLabel.getStyleClass().add("status-outbid");
                }
            }
        } else {
            winnerLabel.setText("Chưa có người đặt giá");
            if (isBidder) {
                userStatusLabel.setText("Hãy là người đầu tiên đặt giá!");
                userStatusLabel.getStyleClass().removeAll("status-leading", "status-outbid");
            }
        }

        // Disable bidding nếu auction không đang chạy
        boolean isRunning = "RUNNING".equals(status);
        placeBidButton.setDisable(!isRunning || !isBidder);
        autoBidButton.setDisable(!isRunning || !isBidder);
        bidAmountField.setDisable(!isRunning || !isBidder);
        maxBidField.setDisable(!isRunning || !isBidder);
        incrementField.setDisable(!isRunning || !isBidder);

        if ("FINISHED".equals(status) && !isFinishedAlertShown) {
            isFinishedAlertShown = true;
            Platform.runLater(() -> {
                String winnerFullname = (winner != null && !winner.isNull()) ? winner.path("fullname").asText("") : "";
                String winnerName = !winnerFullname.isBlank() ? winnerFullname
                        : (winner != null ? winner.path("username").asText() : "");
                String msg = (winner != null && !winner.isNull())
                        ? "Phiên đấu giá kết thúc! Người thắng: " + winnerName
                        : "Phiên đấu giá kết thúc mà không có người mua.";
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo kết thúc");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.show();
            });
        }

        // Cập nhật gợi ý giá bid tối thiểu (giả định buóc giá 50k nếu chưa biết)
        BigDecimal minNextBid = price.add(new BigDecimal("50000"));
        minBidLabel.setText(String.format("Gợi ý: Đặt từ %,.0f VND", minNextBid));
        if (bidAmountField.getText().isEmpty()) {
            bidAmountField.setText(minNextBid.toPlainString());
        }

        checkAutoBidStatus();
    }

    private void checkAutoBidStatus() {
        if (!SessionManager.getInstance().isBidder())
            return;
        Long userId = SessionManager.getInstance().getUserId();
        if (userId == null)
            return;

        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance()
                        .get("/auctions/" + auctionId + "/auto-bid/status?bidderId=" + userId);

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        try {
                            JsonNode node = mapper.readTree(response.body());
                            boolean isActive = node.path("active").asBoolean(false);
                            autoBidButton.setVisible(!isActive);
                            autoBidButton.setManaged(!isActive);
                            stopAutoBidButton.setVisible(isActive);
                            stopAutoBidButton.setManaged(isActive);
                        } catch (Exception ignored) {
                        }
                    }
                });
            } catch (Exception ignored) {
            }
        }).start();
    }

    // ==================== Actions ====================

    /**
     * Xử lý chức năng bấm nút "Đặt giá".
     * Lấy số tiền nhập vào, kiểm tra, và gọi API POST /auctions/{id}/bid để yêu cầu
     * đặt giá.
     */
    @FXML
    private void onPlaceBid() {
        try {
            // Lấy số tiền người dùng nhập
            String amountText = bidAmountField.getText().trim();
            if (amountText.isEmpty()) {
                messageLabel.setText("Vui lòng nhập số tiền!");
                return;
            }

            BigDecimal amount = new BigDecimal(amountText);
            Long userId = SessionManager.getInstance().getUserId();

            placeBidButton.setDisable(true);
            messageLabel.setText("Đang đặt giá...");

            new Thread(() -> {
                try {
                    JSONObject body = new JSONObject();
                    body.put("bidderId", userId);
                    body.put("amount", amount);

                    HttpResponse<String> response = BackendClient.getInstance()
                            .post("/auctions/" + auctionId + "/bid", body.toString());

                    Platform.runLater(() -> {
                        // Tùy vào API trả về thành công (status 200) hay thất bại, hiện thông báo màu
                        // xanh (thành công) hoặc đỏ (lỗi).
                        if (response.statusCode() == 200) {
                            messageLabel.setText("Đặt giá thành công!");
                            messageLabel.setStyle("-fx-text-fill: #22c55e;");
                            bidAmountField.clear();
                            loadAuctionDetail();
                            loadBidHistory();
                        } else {
                            messageLabel.setText("Lỗi: " + response.body());
                            messageLabel.setStyle("-fx-text-fill: #ef4444;");
                        }
                        placeBidButton.setDisable(false);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        messageLabel.setText("Lỗi kết nối: " + e.getMessage());
                        messageLabel.setStyle("-fx-text-fill: #ef4444;");
                        placeBidButton.setDisable(false);
                    });
                }
            }).start();
        } catch (NumberFormatException e) {
            messageLabel.setText("Số tiền không hợp lệ!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    /**
     * Xử lý chức năng Auto-Bid (Đặt giá tự động).
     * Gọi API POST /auctions/{id}/auto-bid để báo cho server tự động bid hộ.
     */
    @FXML
    private void onAutoBid() {
        try {
            // Lấy 2 thông số người dùng nhập: maxBid (Giá tối đa chịu mua) và increment
            // (Bước giá tự động tăng)
            String maxBidText = maxBidField.getText().trim();
            String incrementText = incrementField.getText().trim();

            if (maxBidText.isEmpty() || incrementText.isEmpty()) {
                messageLabel.setText("Vui lòng nhập giá tối đa và bước giá!");
                return;
            }

            BigDecimal maxBid = new BigDecimal(maxBidText);
            BigDecimal increment = new BigDecimal(incrementText);
            Long userId = SessionManager.getInstance().getUserId();

            autoBidButton.setDisable(true);
            messageLabel.setText("Đang đăng ký auto-bid...");

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
                            messageLabel.setText("Đăng ký auto-bid thành công!");
                            messageLabel.setStyle("-fx-text-fill: #22c55e;");
                            maxBidField.clear();
                            incrementField.clear();
                        } else {
                            messageLabel.setText("Lỗi: " + response.body());
                            messageLabel.setStyle("-fx-text-fill: #ef4444;");
                        }
                        autoBidButton.setDisable(false);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        messageLabel.setText("Lỗi kết nối: " + e.getMessage());
                        messageLabel.setStyle("-fx-text-fill: #ef4444;");
                        autoBidButton.setDisable(false);
                    });
                }
            }).start();
        } catch (NumberFormatException e) {
            messageLabel.setText("Số tiền không hợp lệ!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    /**
     * Xử lý chức năng nút Quay lại.
     * Dọn dẹp các tiến trình ngầm và chuyển về trang danh sách.
     */
    @FXML
    private void onStopAutoBid() {
        Long userId = SessionManager.getInstance().getUserId();
        if (userId == null)
            return;

        stopAutoBidButton.setDisable(true);
        messageLabel.setText("Đang dừng auto-bid...");

        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("bidderId", userId);

                HttpResponse<String> response = BackendClient.getInstance()
                        .post("/auctions/" + auctionId + "/auto-bid/stop", body.toString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        messageLabel.setText("Đã dừng auto-bid!");
                        messageLabel.setStyle("-fx-text-fill: #22c55e;");
                        checkAutoBidStatus();
                    } else {
                        messageLabel.setText("Lỗi: " + response.body());
                    }
                    stopAutoBidButton.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    messageLabel.setText("Lỗi kết nối: " + e.getMessage());
                    stopAutoBidButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void onStartAuction() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn bắt đầu phiên đấu giá này?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        HttpResponse<String> response = BackendClient.getInstance()
                                .post("/auctions/" + auctionId + "/start", "");
                        Platform.runLater(() -> {
                            if (response.statusCode() == 200) {
                                messageLabel.setText("Phiên đấu giá đã bắt đầu!");
                                messageLabel.setStyle("-fx-text-fill: #22c55e;");
                                loadAuctionDetail();
                            } else {
                                messageLabel.setText("Lỗi: " + response.body());
                                messageLabel.setStyle("-fx-text-fill: #ef4444;");
                            }
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            messageLabel.setText("Lỗi kết nối: " + e.getMessage());
                            messageLabel.setStyle("-fx-text-fill: #ef4444;");
                        });
                    }
                }).start();
            }
        });
    }

    @FXML
    private void onEndAuctionEarly() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn kết thúc phiên đấu giá này sớm?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                Long userId = SessionManager.getInstance().getUserId();
                String role = SessionManager.getInstance().getRole();
                new Thread(() -> {
                    try {
                        HttpResponse<String> response = BackendClient.getInstance()
                                .post("/auctions/" + auctionId + "/end?userId=" + userId + "&role=" + role, "");
                        Platform.runLater(() -> {
                            if (response.statusCode() == 200) {
                                messageLabel.setText("Đã kết thúc phiên đấu giá!");
                                loadAuctionDetail();
                            } else {
                                messageLabel.setText("Lỗi: " + response.body());
                            }
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> messageLabel.setText("Lỗi kết nối: " + e.getMessage()));
                    }
                }).start();
            }
        });
    }

    @FXML
    private void onDeleteAuction() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn XÓA phiên đấu giá này?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                Long userId = SessionManager.getInstance().getUserId();
                String role = SessionManager.getInstance().getRole();
                new Thread(() -> {
                    try {
                        HttpResponse<String> response = BackendClient.getInstance()
                                .delete("/auctions/" + auctionId + "?userId=" + userId + "&role=" + role);
                        Platform.runLater(() -> {
                            if (response.statusCode() == 200) {
                                onBack(); // Go back after delete
                            } else {
                                messageLabel.setText("Lỗi: " + response.body());
                            }
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> messageLabel.setText("Lỗi kết nối: " + e.getMessage()));
                    }
                }).start();
            }
        });
    }

    @FXML
    private void onBack() {
        // Dừng đếm ngược và ngắt kết nối websocket trước khi đổi màn hình
        if (countdownTimeline != null)
            countdownTimeline.stop();
        WebSocketClient.getInstance().stopPolling();
        try {
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            SceneUtils.changeScene(stage, "/fxml/dashboard.fxml", "Auction Web - Dashboard", "/style/dashboard.css");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== Helpers ====================

    private String formatTime(String isoTime) {
        if (isoTime == null || isoTime.isBlank())
            return "-";
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
