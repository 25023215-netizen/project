package com.nhom4project.auctionweb.frontend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom4project.auctionweb.frontend.utils.BackendClient;
import com.nhom4project.auctionweb.frontend.utils.SceneUtils;
import com.nhom4project.auctionweb.frontend.utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Controller cho Admin Dashboard.
 * Hiển thị thống kê, quản lý users và auctions.
 */
public class AdminDashboardController {

    @FXML private Label adminInfoLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalAuctionsLabel;
    @FXML private Label runningAuctionsLabel;
    @FXML private Label highestBidLabel;
    @FXML private Label statusLabel;

    // History Table
    @FXML private TextField historySearchField;
    @FXML private TableView<String[]> historyTable;
    @FXML private TableColumn<String[], String> historyIdCol;
    @FXML private TableColumn<String[], String> historyTitleCol;
    @FXML private TableColumn<String[], String> historyCategoryCol;
    @FXML private TableColumn<String[], String> historyStartPriceCol;
    @FXML private TableColumn<String[], String> historyWinPriceCol;
    @FXML private TableColumn<String[], String> historyWinnerCol;
    @FXML private TableColumn<String[], String> historySellerCol;
    @FXML private TableColumn<String[], String> historyEndTimeCol;
    @FXML private TableColumn<String[], String> historyActionCol;

    // User Table
    @FXML private TableView<String[]> userTable;
    @FXML private TableColumn<String[], String> userIdCol;
    @FXML private TableColumn<String[], String> userNameCol;
    @FXML private TableColumn<String[], String> userFullnameCol;
    @FXML private TableColumn<String[], String> userEmailCol;
    @FXML private TableColumn<String[], String> userRoleCol;
    @FXML private TableColumn<String[], String> userStatusCol;
    @FXML private TableColumn<String[], String> userActionCol;

    // Auction Table
    @FXML private TableView<String[]> auctionTable;
    @FXML private TableColumn<String[], String> auctionIdCol;
    @FXML private TableColumn<String[], String> auctionTitleCol;
    @FXML private TableColumn<String[], String> auctionCategoryCol;
    @FXML private TableColumn<String[], String> auctionPriceCol;
    @FXML private TableColumn<String[], String> auctionBidsCol;
    @FXML private TableColumn<String[], String> auctionStatusCol;
    @FXML private TableColumn<String[], String> auctionActionCol;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final ObjectMapper mapper = new ObjectMapper();
    private Timeline autoRefreshTimeline;
    private String lastUsersJson = "";
    private String lastAuctionsJson = "";
    private String lastStatsJson = "";
    private final ObservableList<String[]> historyList = FXCollections.observableArrayList();
    private final ObservableList<String[]> filteredHistoryList = FXCollections.observableArrayList();
    private String lastHistoryJson = "";

    @FXML
    public void initialize() {
        if (SessionManager.getInstance().isLoggedIn()) {
            adminInfoLabel.setText("Admin: " + SessionManager.getInstance().getFullname());
        }

        // Setup User table columns
        userIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        userIdCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(getTableRow().getIndex() + 1));
                }
            }
        });
        userNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        userFullnameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        userEmailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));
        userRoleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[4]));
        userStatusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[5]));
        userActionCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                String[] row = getTableRow().getItem();
                String role = row[4];
                if ("ADMIN".equals(role)) {
                    setGraphic(new Label("-"));
                    return;
                }
                Button lockBtn = new Button(row[5].equals("Active") ? "🔒 Khóa" : "🔓 Mở khóa");
                lockBtn.getStyleClass().add("lock-btn");
                lockBtn.setOnAction(e -> toggleLockUser(row[0]));
                Button deleteBtn = new Button("🗑 Xóa");
                deleteBtn.getStyleClass().add("delete-btn");
                deleteBtn.setOnAction(e -> deleteUser(row[0]));
                setGraphic(new HBox(5, lockBtn, deleteBtn));
            }
        });

        // Setup Auction table columns
        auctionIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        auctionIdCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(getTableRow().getIndex() + 1));
                }
            }
        });
        auctionTitleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        auctionCategoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        auctionPriceCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));
        auctionBidsCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[4]));
        auctionStatusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[5]));
        auctionActionCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                String[] row = getTableRow().getItem();
                String status = row[5];
                HBox actions = new HBox(5);
                
                if ("PENDING".equals(status)) {
                    Button approveBtn = new Button("✅ Duyệt");
                    approveBtn.getStyleClass().add("approve-btn");
                    approveBtn.setOnAction(e -> approveAuction(row[0]));
                    Button rejectBtn = new Button("❌ Từ chối");
                    rejectBtn.getStyleClass().add("reject-btn");
                    rejectBtn.setOnAction(e -> rejectAuction(row[0]));
                    actions.getChildren().addAll(approveBtn, rejectBtn);
                }
                
                Button delBtn = new Button("🗑 Xóa");
                delBtn.getStyleClass().add("delete-btn");
                delBtn.setOnAction(e -> deleteAuction(row[0]));
                actions.getChildren().add(delBtn);
                
                setGraphic(actions);
            }
        });

        // Setup History table columns
        historyIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        historyIdCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(getTableRow().getIndex() + 1));
                }
            }
        });
        historyTitleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        historyCategoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        historyStartPriceCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));
        historyWinPriceCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[4]));
        historyWinnerCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[5]));
        historySellerCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[6]));
        historyEndTimeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[7]));
        historyActionCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                String[] row = getTableRow().getItem();
                Button delBtn = new Button("🗑 Xóa");
                delBtn.getStyleClass().add("delete-btn");
                delBtn.setOnAction(e -> deleteHistory(row[0]));
                setGraphic(delBtn);
            }
        });

        historyTable.setItems(filteredHistoryList);
        historySearchField.textProperty().addListener((obs, oldVal, newVal) -> applyHistoryFilter());

        loadStats();
        loadUsers();
        loadAuctions();
        loadHistory();
        startAutoRefresh();
    }

    private void startAutoRefresh() {
        if (autoRefreshTimeline == null) {
            autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                loadStats();
                loadUsers();
                loadAuctions();
                loadHistory();
            }));
            autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
            autoRefreshTimeline.play();
        }
    }

    private void stopAutoRefresh() {
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
            autoRefreshTimeline = null;
        }
    }

    // ==================== Data Loading ====================

    private void loadStats() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().get("/admin/stats");
                if (response.statusCode() == 200) {
                    String body = response.body();
                    if (!body.equals(lastStatsJson)) {
                        lastStatsJson = body;
                        JsonNode stats = mapper.readTree(body);
                        javafx.application.Platform.runLater(() -> {
                            totalUsersLabel.setText(String.valueOf(stats.path("totalUsers").asInt()));
                            totalAuctionsLabel.setText(String.valueOf(stats.path("totalAuctions").asInt()));
                            runningAuctionsLabel.setText(String.valueOf(stats.path("runningAuctions").asInt()));
                            highestBidLabel.setText(currencyFormat.format(new BigDecimal(stats.path("highestBid").asText("0"))));
                        });
                    }
                }
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Lỗi tải thống kê: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void onRefreshUsers() {
        loadUsers();
        loadStats();
    }

    @FXML
    private void onRefreshAuctions() {
        loadAuctions();
        loadStats();
    }

    private void loadUsers() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().get("/admin/users");
                if (response.statusCode() == 200) {
                    String body = response.body();
                    if (!body.equals(lastUsersJson)) {
                        lastUsersJson = body;
                        JsonNode root = mapper.readTree(body);
                        ObservableList<String[]> rows = FXCollections.observableArrayList();
                        for (JsonNode node : root) {
                            rows.add(new String[]{
                                    String.valueOf(node.path("id").asLong()),
                                    node.path("username").asText(),
                                    node.path("fullname").asText(),
                                    node.path("email").asText(),
                                    node.path("role").asText(),
                                    node.path("locked").asBoolean() ? "Locked" : "Active"
                            });
                        }
                        javafx.application.Platform.runLater(() -> {
                            userTable.setItems(rows);
                            statusLabel.setText("Đã tải " + rows.size() + " users.");
                        });
                    }
                }
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Lỗi tải users: " + e.getMessage()));
            }
        }).start();
    }

    private void loadAuctions() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().get("/admin/auctions");
                if (response.statusCode() == 200) {
                    String body = response.body();
                    if (!body.equals(lastAuctionsJson)) {
                        lastAuctionsJson = body;
                        JsonNode root = mapper.readTree(body);
                        ObservableList<String[]> rows = FXCollections.observableArrayList();
                        for (JsonNode node : root) {
                            BigDecimal price = new BigDecimal(node.path("currentPrice").asText("0"));
                            rows.add(new String[]{
                                    String.valueOf(node.path("id").asLong()),
                                    node.path("title").asText(),
                                    node.path("category").asText(),
                                    currencyFormat.format(price),
                                    String.valueOf(node.path("bidCount").asInt()),
                                    node.path("status").asText()
                            });
                        }
                        javafx.application.Platform.runLater(() -> {
                            auctionTable.setItems(rows);
                            statusLabel.setText("Đã tải " + rows.size() + " phiên đấu giá.");
                        });
                    }
                }
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Lỗi tải auctions: " + e.getMessage()));
            }
        }).start();
    }

    // ==================== User Actions ====================

    private void toggleLockUser(String userId) {
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().post("/admin/users/" + userId + "/lock", "");
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText(response.body());
                    loadUsers();
                    loadStats();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Lỗi: " + e.getMessage()));
            }
        }).start();
    }

    private void deleteUser(String userId) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa user này?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        HttpResponse<String> response = BackendClient.getInstance().delete("/admin/users/" + userId);
                        javafx.application.Platform.runLater(() -> {
                            statusLabel.setText(response.body());
                            loadUsers();
                            loadStats();
                        });
                    } catch (Exception e) {
                        javafx.application.Platform.runLater(() -> statusLabel.setText("Lỗi: " + e.getMessage()));
                    }
                }).start();
            }
        });
    }

    // ==================== Auction Actions ====================

    private void approveAuction(String auctionId) {
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().post("/admin/auctions/" + auctionId + "/approve", "");
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText(response.body());
                    loadAuctions();
                    loadStats();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Lỗi: " + e.getMessage()));
            }
        }).start();
    }

    private void rejectAuction(String auctionId) {
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().post("/admin/auctions/" + auctionId + "/reject", "");
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText(response.body());
                    loadAuctions();
                    loadStats();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Lỗi: " + e.getMessage()));
            }
        }).start();
    }

    private void deleteAuction(String auctionId) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa phiên đấu giá này?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        Long userId = SessionManager.getInstance().getUserId();
                        String role = SessionManager.getInstance().getRole();
                        HttpResponse<String> response = BackendClient.getInstance()
                                .delete("/auctions/" + auctionId + "?userId=" + userId + "&role=" + role);
                        javafx.application.Platform.runLater(() -> {
                            statusLabel.setText(response.body());
                            loadAuctions();
                            loadStats();
                        });
                    } catch (Exception e) {
                        javafx.application.Platform.runLater(() -> statusLabel.setText("Lỗi: " + e.getMessage()));
                    }
                }).start();
            }
        });
    }

    // ==================== Navigation ====================

    @FXML
    private void onLogout() {
        stopAutoRefresh();
        SessionManager.getInstance().clear();
        try {
            Stage stage = (Stage) adminInfoLabel.getScene().getWindow();
            SceneUtils.changeScene(stage, "/fxml/signin.fxml", "Online Auction System - Sign In", "/style/signin.css");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== History Actions ====================

    @FXML
    private void onRefreshHistory() {
        loadHistory();
    }

    private void loadHistory() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().get("/admin/auctions/history/all");
                if (response.statusCode() == 200) {
                    String body = response.body();
                    if (!body.equals(lastHistoryJson)) {
                        lastHistoryJson = body;
                        JsonNode root = mapper.readTree(body);
                        ObservableList<String[]> rows = FXCollections.observableArrayList();
                        for (JsonNode node : root) {
                            BigDecimal startPrice = new BigDecimal(node.path("startingPrice").asText("0"));
                            BigDecimal winPrice = new BigDecimal(node.path("winningPrice").asText("0"));
                            
                            // Format End Time nicely
                            String endTimeStr = node.path("endTime").asText();
                            String formattedEndTime = "-";
                            if (endTimeStr != null && !endTimeStr.isBlank()) {
                                try {
                                    java.time.LocalDateTime time = java.time.LocalDateTime.parse(endTimeStr);
                                    formattedEndTime = time.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                                } catch (Exception ignored) {
                                    formattedEndTime = endTimeStr;
                                }
                            }

                            rows.add(new String[]{
                                    String.valueOf(node.path("id").asLong()),
                                    node.path("title").asText(),
                                    node.path("category").asText(),
                                    currencyFormat.format(startPrice),
                                    currencyFormat.format(winPrice),
                                    node.path("winnerName").asText("-"),
                                    node.path("sellerName").asText("-"),
                                    formattedEndTime
                            });
                        }
                        javafx.application.Platform.runLater(() -> {
                            historyList.setAll(rows);
                            applyHistoryFilter();
                        });
                    }
                }
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Lỗi tải lịch sử: " + e.getMessage()));
            }
        }).start();
    }

    private void applyHistoryFilter() {
        String keyword = historySearchField.getText() == null ? "" : historySearchField.getText().trim().toLowerCase();
        ObservableList<String[]> filtered = FXCollections.observableArrayList();
        for (String[] row : historyList) {
            if (keyword.isEmpty()
                    || row[1].toLowerCase().contains(keyword) // Title
                    || row[2].toLowerCase().contains(keyword) // Category
                    || row[5].toLowerCase().contains(keyword) // Winner
                    || row[6].toLowerCase().contains(keyword)) { // Seller
                filtered.add(row);
            }
        }
        filteredHistoryList.setAll(filtered);
    }

    private void deleteHistory(String historyId) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa lịch sử đấu giá này?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        HttpResponse<String> response = BackendClient.getInstance().delete("/admin/auctions/history/" + historyId);
                        javafx.application.Platform.runLater(() -> {
                            statusLabel.setText(response.body());
                            loadHistory();
                        });
                    } catch (Exception e) {
                        javafx.application.Platform.runLater(() -> statusLabel.setText("Lỗi: " + e.getMessage()));
                    }
                }).start();
            }
        });
    }
}
