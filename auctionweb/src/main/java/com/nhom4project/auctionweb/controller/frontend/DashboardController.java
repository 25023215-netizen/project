package com.nhom4project.auctionweb.controller.frontend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom4project.auctionweb.controller.frontend.AuctionDetailController;
import com.nhom4project.auctionweb.client.utils.BackendClient;
import com.nhom4project.auctionweb.client.utils.SceneUtils;
import com.nhom4project.auctionweb.client.utils.SessionManager;
import com.nhom4project.auctionweb.client.utils.WindowUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
=======
import javafx.scene.input.MouseButton;
import javafx.scene.control.TableRow;
import javafx.stage.Stage;

=======
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.concurrent.CompletableFuture;
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
=======
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Controller cho Dashboard chính.
 * Hiển thị danh sách phiên đấu giá, thống kê, tìm kiếm.
 * Click vào row để mở chi tiết phiên đấu giá.
 */
public class DashboardController {
    @FXML private Label totalAuctionsLabel;
    @FXML private Label runningAuctionsLabel;
    @FXML private Label totalBidLabel;
    @FXML private Label statusLabel;
    @FXML private Label userInfoLabel;
    @FXML private TextField searchField;

    @FXML private TableView<AuctionRow> auctionTable;
    @FXML private TableColumn<AuctionRow, String> titleColumn;
    @FXML private TableColumn<AuctionRow, String> categoryColumn;
    @FXML private TableColumn<AuctionRow, String> priceColumn;
    @FXML private TableColumn<AuctionRow, String> bidsColumn;
    @FXML private TableColumn<AuctionRow, String> statusColumn;
    @FXML private TableColumn<AuctionRow, String> endTimeColumn;

    @FXML private Button manageItemsButton;

<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
    // Các thành phần FXML mới cho Lịch sử Kết quả
    @FXML private Button dashboardButton;
    @FXML private Button historyNavButton;
    @FXML private VBox auctionPanel;
    @FXML private VBox historyPanel;
    @FXML private TextField historySearchField;
=======
    // History Table bindings
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
    @FXML private TableView<HistoryRow> historyTable;
    @FXML private TableColumn<HistoryRow, String> historyTitleColumn;
    @FXML private TableColumn<HistoryRow, String> historyCategoryColumn;
    @FXML private TableColumn<HistoryRow, String> historyStartPriceColumn;
    @FXML private TableColumn<HistoryRow, String> historyWinPriceColumn;
    @FXML private TableColumn<HistoryRow, String> historyWinnerColumn;
    @FXML private TableColumn<HistoryRow, String> historySellerColumn;
    @FXML private TableColumn<HistoryRow, String> historyEndTimeColumn;
    @FXML private TextField historySearchField;

    private Timeline autoRefreshTimeline;
    private final ObservableList<AuctionRow> auctions = FXCollections.observableArrayList();
    private final ObservableList<AuctionRow> filteredAuctions = FXCollections.observableArrayList();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
=======
    private Timeline autoRefreshTimeline;
    private final ObservableList<AuctionRow> auctions = FXCollections.observableArrayList();
    private final ObservableList<AuctionRow> filteredAuctions = FXCollections.observableArrayList();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final ObjectMapper mapper = new ObjectMapper();
    private String lastResponseJson = "";
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
=======
    private final ObjectMapper mapper = new ObjectMapper();
    private String lastResponseJson = "";
    private final ObservableList<HistoryRow> historyList = FXCollections.observableArrayList();
    private final ObservableList<HistoryRow> filteredHistoryList = FXCollections.observableArrayList();
    private String lastHistoryResponseJson = "";
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(data -> data.getValue().titleProperty());
        categoryColumn.setCellValueFactory(data -> data.getValue().categoryProperty());
        priceColumn.setCellValueFactory(data -> data.getValue().priceProperty());
        bidsColumn.setCellValueFactory(data -> data.getValue().bidsProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        endTimeColumn.setCellValueFactory(data -> data.getValue().endTimeProperty());

        auctionTable.setItems(filteredAuctions);
        auctionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilter());

<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
        // Thiết lập bảng lịch sử
        if (historyTitleColumn != null) {
            historyTitleColumn.setCellValueFactory(data -> data.getValue().titleProperty());
            historyCategoryColumn.setCellValueFactory(data -> data.getValue().categoryProperty());
            historyStartPriceColumn.setCellValueFactory(data -> data.getValue().startingPriceProperty());
            historyEndPriceColumn.setCellValueFactory(data -> data.getValue().winningPriceProperty());
            historyWinnerColumn.setCellValueFactory(data -> data.getValue().winnerNameProperty());
            historySellerColumn.setCellValueFactory(data -> data.getValue().sellerNameProperty());
            historyEndTimeColumn.setCellValueFactory(data -> data.getValue().endTimeProperty());
=======
        // Setup History Table
        historyTitleColumn.setCellValueFactory(data -> data.getValue().titleProperty());
        historyCategoryColumn.setCellValueFactory(data -> data.getValue().categoryProperty());
        historyStartPriceColumn.setCellValueFactory(data -> data.getValue().startingPriceProperty());
        historyWinPriceColumn.setCellValueFactory(data -> data.getValue().winningPriceProperty());
        historyWinnerColumn.setCellValueFactory(data -> data.getValue().winnerNameProperty());
        historySellerColumn.setCellValueFactory(data -> data.getValue().sellerNameProperty());
        historyEndTimeColumn.setCellValueFactory(data -> data.getValue().endTimeProperty());
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java

        historyTable.setItems(filteredHistoryList);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        if (historySearchField != null) {
            historySearchField.textProperty().addListener((observable, oldValue, newValue) -> applyHistoryFilter());
        }

        // Double-click vào row để mở chi tiết
        auctionTable.setOnMouseClicked(this::onTableClick);
=======
        // Double-click vào row để mở chi tiết
        auctionTable.setRowFactory(tv -> {
            TableRow<AuctionRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    AuctionRow rowData = row.getItem();
                    openAuctionDetail(rowData.getId());
                }
            });
            return row;
        });
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java

        // Hiển thị thông tin user
        if (SessionManager.getInstance().isLoggedIn()) {
            if (userInfoLabel != null) {
                userInfoLabel.setText("Xin chao, " + SessionManager.getInstance().getFullname()
                        + " (" + SessionManager.getInstance().getRole() + ")");
            }
        }

        // Chỉ hiện nút quản lý sản phẩm cho Seller
        if (manageItemsButton != null) {
            manageItemsButton.setVisible(SessionManager.getInstance().isSeller());
            manageItemsButton.setManaged(SessionManager.getInstance().isSeller());
        }

        loadAuctions();
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
=======
=======
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
        startAutoRefresh();
    }

    private void startAutoRefresh() {
        if (autoRefreshTimeline == null) {
            autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> loadAuctions()));
            autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
            autoRefreshTimeline.play();
        }
    }

    private void stopAutoRefresh() {
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
            autoRefreshTimeline = null;
        }
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
=======
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
    }

    @FXML
    private void onRefresh() {
        loadAuctions();
    }

    /**
     * Mở màn hình quản lý sản phẩm (dành cho Seller).
     */
    @FXML
    private void onManageItems() {
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
=======
        stopAutoRefresh();
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
=======
        stopAutoRefresh();
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
        try {
            Stage stage = (Stage) auctionTable.getScene().getWindow();
            SceneUtils.changeScene(stage, "/fxml/item_management.fxml", "Quan ly san pham", "/style/item_management.css");
            stage.setMinWidth(980);
            stage.setMinHeight(680);
        } catch (Exception e) {
            statusLabel.setText("Loi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Đăng xuất và quay lại màn hình đăng nhập.
     */
    @FXML
    private void onLogout() {
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
=======
        stopAutoRefresh();
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
=======
        stopAutoRefresh();
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
        SessionManager.getInstance().clear();
        try {
            Stage stage = (Stage) auctionTable.getScene().getWindow();
            SceneUtils.changeScene(stage, "/fxml/signin.fxml", "Online Auction System - Sign In", "/style/signin.css");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
    /**
     * Double-click vào row -> mở chi tiết phiên đấu giá.
     */
    private void onTableClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            AuctionRow selected = auctionTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openAuctionDetail(selected.getId());
            }
        }
    }

    private void openAuctionDetail(Long auctionId) {
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
=======
    // Double-click handled in row factory above

    private void openAuctionDetail(Long auctionId) {
        stopAutoRefresh();
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
=======
        stopAutoRefresh();
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
        try {
            Stage stage = (Stage) auctionTable.getScene().getWindow();
            AuctionDetailController controller = SceneUtils.changeSceneWithController(stage, "/fxml/auction_detail.fxml", "Chi tiet phien dau gia", "/style/auction_detail.css");
            if (controller != null) {
                controller.setAuctionId(auctionId);
            }
        } catch (Exception e) {
            statusLabel.setText("Loi: " + e.getMessage());
            e.printStackTrace();
        }
    }

<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
    private void loadAuctions() {
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
        try {
            HttpResponse<String> response = BackendClient.getInstance().get("/auctions");
            if (response.statusCode() == 200) {
                auctions.setAll(parseAuctions(response.body()));
                statusLabel.setText("Da tai danh sach dau gia tu server.");
            } else {
                loadFallbackAuctions("Server tra ve loi: " + response.statusCode());
=======
    private void loadHistory() {
        CompletableFuture.runAsync(() -> {
            try {
                HttpResponse<String> mainResp = BackendClient.getInstance().get("/auctions");
                HttpResponse<String> histResp = BackendClient.getInstance().get("/auctions/history");
                if (mainResp.statusCode() == 200) {
                    String mainBody = mainResp.body();
                    String histBody = (histResp.statusCode() == 200) ? histResp.body() : "[]";
                    String uniqueKey = mainBody.hashCode() + "-" + histBody.hashCode();
                    if (!uniqueKey.equals(lastHistoryResponseJson)) {
                        lastHistoryResponseJson = uniqueKey;
                        ObservableList<HistoryRow> parsed = parseHistory(mainBody, histBody);
                        Platform.runLater(() -> {
                            historyList.setAll(parsed);
                            applyHistoryFilter();
                        });
                    }
                } else {
                    Platform.runLater(() -> {
                        loadFallbackHistory();
                        applyHistoryFilter();
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadFallbackHistory();
                    applyHistoryFilter();
                });
            }
        });
    }

    private ObservableList<HistoryRow> parseHistory(String mainBody, String histBody) throws Exception {
        ObservableList<HistoryRow> rows = FXCollections.observableArrayList();
        JsonNode mainRoot = mapper.readTree(mainBody);
        for (JsonNode node : mainRoot) {
            String status = node.path("status").asText();
            if ("FINISHED".equalsIgnoreCase(status) || "PAID".equalsIgnoreCase(status) || "CANCELED".equalsIgnoreCase(status)) {
                String title = node.path("title").asText();
                String category = node.path("category").asText();
                BigDecimal startPrice = new BigDecimal(node.path("startingPrice").asText("0"));
                BigDecimal winPrice = new BigDecimal(node.path("currentPrice").asText("0"));
                JsonNode winnerNode = node.path("winner");
                String winner = (winnerNode != null && !winnerNode.isNull() && !winnerNode.isMissingNode())
                        ? winnerNode.path("username").asText("-") : "-";
                JsonNode sellerNode = node.path("seller");
                String seller = (sellerNode != null && !sellerNode.isNull() && !sellerNode.isMissingNode())
                        ? sellerNode.path("username").asText("-") : "-";
                String endTime = formatEndTime(node.path("endTime").asText());
                rows.add(new HistoryRow(title, category, currencyFormat.format(startPrice),
                        currencyFormat.format(winPrice), winner, seller, endTime));
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
            }
        }
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
        applyFilter();
        updateStats();
=======
=======
        JsonNode histRoot = mapper.readTree(histBody);
        for (JsonNode node : histRoot) {
            String title = node.path("title").asText();
            String category = node.path("category").asText();
            BigDecimal startPrice = new BigDecimal(node.path("startingPrice").asText("0"));
            BigDecimal winPrice = new BigDecimal(node.path("winningPrice").asText("0"));
            String winner = node.path("winnerName").asText("-");
            String seller = node.path("sellerName").asText("-");
            String endTime = formatEndTime(node.path("endTime").asText());
            rows.add(new HistoryRow("[Đã xóa] " + title, category, currencyFormat.format(startPrice),
                    currencyFormat.format(winPrice), winner, seller, endTime));
        }
        return rows;
    }

    private void loadFallbackHistory() {
        historyList.setAll(
                new HistoryRow("[Đã xóa] Bàn phím cơ Custom", "Electronics", "1.500.000 VND", "2.100.000 VND", "bidder1", "seller1", "20/05/2026 15:30"),
                new HistoryRow("Apple Watch Ultra", "Electronics", "15.000.000 VND", "18.500.000 VND", "vietanh", "seller2", "19/05/2026 18:00")
        );
    }

    private void applyHistoryFilter() {
        String keyword = historySearchField.getText() == null ? "" : historySearchField.getText().trim().toLowerCase();
        filteredHistoryList.setAll(historyList.filtered(row ->
                keyword.isEmpty()
                        || row.titleProperty().get().toLowerCase().contains(keyword)
                        || row.categoryProperty().get().toLowerCase().contains(keyword)
                        || row.winnerNameProperty().get().toLowerCase().contains(keyword)
        ));
    }

    private void loadAuctions() {
        loadHistory();
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
        CompletableFuture.runAsync(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().get("/auctions");
                if (response.statusCode() == 200) {
                    String body = response.body();
                    if (!body.equals(lastResponseJson)) {
                        lastResponseJson = body;
                        ObservableList<AuctionRow> parsed = parseAuctions(body);
                        Platform.runLater(() -> {
                            auctions.setAll(parsed);
                            statusLabel.setText("Da tai danh sach dau gia tu server.");
                            applyFilter();
                            updateStats();
                        });
                    }
                } else {
                    Platform.runLater(() -> {
                        loadFallbackAuctions("Server tra ve loi: " + response.statusCode());
                        applyFilter();
                        updateStats();
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadFallbackAuctions("Dang hien thi du lieu mau vi chua ket noi duoc server.");
                    applyFilter();
                    updateStats();
                });
            }
        });
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
=======
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
    }

    private ObservableList<AuctionRow> parseAuctions(String body) throws Exception {
        ObservableList<AuctionRow> rows = FXCollections.observableArrayList();
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
        JsonNode root = new ObjectMapper().readTree(body);
=======
        JsonNode root = mapper.readTree(body);
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
=======
        JsonNode root = mapper.readTree(body);
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
        for (JsonNode node : root) {
            Long id = node.path("id").asLong();
            String title = node.path("title").asText();
            String category = node.path("category").asText();
            BigDecimal price = new BigDecimal(node.path("currentPrice").asText("0"));
            String bidCount = String.valueOf(node.path("bidCount").asInt());
            String status = node.path("status").asText();
            String endTime = formatEndTime(node.path("endTime").asText());
            rows.add(new AuctionRow(id, title, category, currencyFormat.format(price), bidCount, status, endTime));
        }
        return rows;
    }

    private void loadFallbackAuctions(String message) {
        auctions.setAll(
                new AuctionRow(0L, "iPhone 15 Pro Max 256GB", "Electronics", "25.000.000 VND", "18", "RUNNING", "Con 2 ngay"),
                new AuctionRow(0L, "Tranh Son Dau - Ho Guom", "Art", "5.200.000 VND", "9", "RUNNING", "Con 3 ngay"),
                new AuctionRow(0L, "Honda Wave Alpha 2023", "Vehicle", "15.000.000 VND", "4", "OPEN", "Con 4 ngay")
        );
        statusLabel.setText(message);
    }

    private void applyFilter() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        filteredAuctions.setAll(auctions.filtered(row ->
                keyword.isEmpty()
                        || row.titleProperty().get().toLowerCase().contains(keyword)
                        || row.categoryProperty().get().toLowerCase().contains(keyword)
                        || row.statusProperty().get().toLowerCase().contains(keyword)
        ));
    }

    private void updateStats() {
        totalAuctionsLabel.setText(String.valueOf(auctions.size()));
        long runningCount = auctions.stream()
                .filter(row -> "RUNNING".equalsIgnoreCase(row.statusProperty().get()))
                .count();
        runningAuctionsLabel.setText(String.valueOf(runningCount));
        int totalBids = auctions.stream()
                .mapToInt(row -> {
                    try { return Integer.parseInt(row.bidsProperty().get()); }
                    catch (NumberFormatException e) { return 0; }
                })
                .sum();
        totalBidLabel.setText(String.valueOf(totalBids));
    }

    private String formatEndTime(String value) {
        if (value == null || value.isBlank()) return "-";
        try {
            LocalDateTime time = LocalDateTime.parse(value);
            return time.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception ignored) {
            return value;
        }
    }

    // ==================== Inner Row Class ====================

    public static class AuctionRow {
        private final Long id;
        private final SimpleStringProperty title;
        private final SimpleStringProperty category;
        private final SimpleStringProperty price;
        private final SimpleStringProperty bids;
        private final SimpleStringProperty status;
        private final SimpleStringProperty endTime;

        public AuctionRow(Long id, String title, String category, String price,
                          String bids, String status, String endTime) {
            this.id = id;
            this.title = new SimpleStringProperty(title);
            this.category = new SimpleStringProperty(category);
            this.price = new SimpleStringProperty(price);
            this.bids = new SimpleStringProperty(bids);
            this.status = new SimpleStringProperty(status);
            this.endTime = new SimpleStringProperty(endTime);
        }

        public Long getId() { return id; }
        public SimpleStringProperty titleProperty() { return title; }
        public SimpleStringProperty categoryProperty() { return category; }
        public SimpleStringProperty priceProperty() { return price; }
        public SimpleStringProperty bidsProperty() { return bids; }
        public SimpleStringProperty statusProperty() { return status; }
        public SimpleStringProperty endTimeProperty() { return endTime; }
    }
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java

    public static class HistoryRow {
        private final SimpleStringProperty title;
        private final SimpleStringProperty category;
        private final SimpleStringProperty startingPrice;
        private final SimpleStringProperty winningPrice;
        private final SimpleStringProperty winnerName;
        private final SimpleStringProperty sellerName;
        private final SimpleStringProperty endTime;

        public HistoryRow(String title, String category, String startingPrice,
                          String winningPrice, String winnerName, String sellerName, String endTime) {
            this.title = new SimpleStringProperty(title);
            this.category = new SimpleStringProperty(category);
            this.startingPrice = new SimpleStringProperty(startingPrice);
            this.winningPrice = new SimpleStringProperty(winningPrice);
            this.winnerName = new SimpleStringProperty(winnerName);
            this.sellerName = new SimpleStringProperty(sellerName);
            this.endTime = new SimpleStringProperty(endTime);
        }

        public SimpleStringProperty titleProperty() { return title; }
        public SimpleStringProperty categoryProperty() { return category; }
        public SimpleStringProperty startingPriceProperty() { return startingPrice; }
        public SimpleStringProperty winningPriceProperty() { return winningPrice; }
        public SimpleStringProperty winnerNameProperty() { return winnerName; }
        public SimpleStringProperty sellerNameProperty() { return sellerName; }
        public SimpleStringProperty endTimeProperty() { return endTime; }
    }
=======
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/DashboardController.java
}




