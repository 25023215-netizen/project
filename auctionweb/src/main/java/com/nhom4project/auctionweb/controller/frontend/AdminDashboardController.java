package com.nhom4project.auctionweb.controller.frontend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom4project.auctionweb.client.utils.BackendClient;
import com.nhom4project.auctionweb.client.utils.SessionManager;
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

/**
 * Controller cho Admin Dashboard.
 * Hiển thị thống kê, quản lý users và auctions.
 */
public class AdminDashboardController {

    @FXML private Label adminInfoLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalAuctionsLabel;
    @FXML private Label runningAuctionsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label highestBidLabel;
    @FXML private Label statusLabel;

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

    @FXML
    public void initialize() {
        if (SessionManager.getInstance().isLoggedIn()) {
            adminInfoLabel.setText("Admin: " + SessionManager.getInstance().getFullname());
        }

        // Setup User table columns
        userIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
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
                Button lockBtn = new Button(row[5].equals("Active") ? "🔒 Lock" : "🔓 Unlock");
                lockBtn.getStyleClass().add("lock-btn");
                lockBtn.setOnAction(e -> toggleLockUser(row[0]));
                Button deleteBtn = new Button("🗑 Delete");
                deleteBtn.getStyleClass().add("delete-btn");
                deleteBtn.setOnAction(e -> deleteUser(row[0]));
                setGraphic(new HBox(5, lockBtn, deleteBtn));
            }
        });

        // Setup Auction table columns
        auctionIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
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
                if ("PENDING".equals(status)) {
                    Button approveBtn = new Button("✅ Approve");
                    approveBtn.getStyleClass().add("approve-btn");
                    approveBtn.setOnAction(e -> approveAuction(row[0]));
                    Button rejectBtn = new Button("❌ Reject");
                    rejectBtn.getStyleClass().add("reject-btn");
                    rejectBtn.setOnAction(e -> rejectAuction(row[0]));
                    setGraphic(new HBox(5, approveBtn, rejectBtn));
                } else {
                    setGraphic(new Label(status));
                }
<<<<<<< Updated upstream:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/AdminDashboardController.java
=======
                
                Button delBtn = new Button("🗑 Delete");
                delBtn.getStyleClass().add("delete-btn");
                delBtn.setOnAction(e -> deleteAuction(row[0]));
                actions.getChildren().add(delBtn);
                
                setGraphic(actions);
>>>>>>> Stashed changes:auctionweb/src/main/java/com/nhom4project/auctionweb/frontend/controller/AdminDashboardController.java
            }
        });

        loadStats();
        loadUsers();
        loadAuctions();
    }

    // ==================== Data Loading ====================

    private void loadStats() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().get("/admin/stats");
                if (response.statusCode() == 200) {
                    JsonNode stats = mapper.readTree(response.body());
                    javafx.application.Platform.runLater(() -> {
                        totalUsersLabel.setText(String.valueOf(stats.path("totalUsers").asInt()));
                        totalAuctionsLabel.setText(String.valueOf(stats.path("totalAuctions").asInt()));
                        runningAuctionsLabel.setText(String.valueOf(stats.path("runningAuctions").asInt()));
                        totalRevenueLabel.setText(currencyFormat.format(new BigDecimal(stats.path("totalRevenue").asText("0"))));
                        highestBidLabel.setText(currencyFormat.format(new BigDecimal(stats.path("highestBid").asText("0"))));
                    });
                }
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Error loading stats: " + e.getMessage()));
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
<<<<<<< Updated upstream:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/AdminDashboardController.java
                    JsonNode root = mapper.readTree(response.body());
                    ObservableList<String[]> rows = FXCollections.observableArrayList();
                    for (JsonNode node : root) {
                        rows.add(new String[]{
                                String.valueOf(node.path("id").asLong()),
                                node.path("username").asText(),
                                node.path("fullname").asText(),
                                node.path("email").asText(),
                                node.path("role").asText(),
                                node.path("locked").asBoolean() ? "Locked" : "Active"
=======
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
                            statusLabel.setText("Loaded " + rows.size() + " users.");
>>>>>>> Stashed changes:auctionweb/src/main/java/com/nhom4project/auctionweb/frontend/controller/AdminDashboardController.java
                        });
                    }
                    javafx.application.Platform.runLater(() -> {
                        userTable.setItems(rows);
                        statusLabel.setText("Đã tải " + rows.size() + " users.");
                    });
                }
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Error loading users: " + e.getMessage()));
            }
        }).start();
    }

    private void loadAuctions() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().get("/admin/auctions");
                if (response.statusCode() == 200) {
<<<<<<< Updated upstream:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/AdminDashboardController.java
                    JsonNode root = mapper.readTree(response.body());
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
=======
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
                            statusLabel.setText("Loaded " + rows.size() + " auctions.");
>>>>>>> Stashed changes:auctionweb/src/main/java/com/nhom4project/auctionweb/frontend/controller/AdminDashboardController.java
                        });
                    }
                    javafx.application.Platform.runLater(() -> {
                        auctionTable.setItems(rows);
                        statusLabel.setText("Đã tải " + rows.size() + " phiên đấu giá.");
                    });
                }
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Error loading auctions: " + e.getMessage()));
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
                javafx.application.Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
            }
        }).start();
    }

    private void deleteUser(String userId) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this user?");
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
                        javafx.application.Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
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
                javafx.application.Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
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
                javafx.application.Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
            }
        }).start();
    }

<<<<<<< Updated upstream:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/AdminDashboardController.java
=======
    private void deleteAuction(String auctionId) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this auction?");
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
                        javafx.application.Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
                    }
                }).start();
            }
        });
    }

>>>>>>> Stashed changes:auctionweb/src/main/java/com/nhom4project/auctionweb/frontend/controller/AdminDashboardController.java
    // ==================== Navigation ====================

    @FXML
    private void onLogout() {
        SessionManager.getInstance().clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/signin.fxml"));
            Stage stage = (Stage) adminInfoLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style/signin.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Online Auction System - Sign In");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
