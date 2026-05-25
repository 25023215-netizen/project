package com.nhom4project.auctionweb.frontend.controller;

import com.nhom4project.auctionweb.frontend.utils.BackendClient;
import com.nhom4project.auctionweb.frontend.utils.SceneUtils;
import com.nhom4project.auctionweb.frontend.utils.SessionManager;
import com.nhom4project.auctionweb.frontend.utils.ErrorLogger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.net.http.HttpResponse;

public class SigninController {
    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signinButton;

    @FXML
    private Label statusLabel;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    public void initialize() {
        updateButtonState();
        userNameField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());
    }

    private void updateButtonState() {
        boolean disable = userNameField.getText().trim().isEmpty() || passwordField.getText().isEmpty();
        signinButton.setDisable(disable);
        if (disable) {
            statusLabel.setText("");
        }
    }

    @FXML
    private void onSignIn(ActionEvent event) {
        String username = userNameField.getText().trim();
        String password = passwordField.getText();
        String rawRole = roleComboBox.getValue();
        String tempRole = "BIDDER";
        if ("Người bán".equals(rawRole) || "Seller".equalsIgnoreCase(rawRole)) {
            tempRole = "SELLER";
        } else if ("Quản trị viên".equals(rawRole) || "Admin".equalsIgnoreCase(rawRole)) {
            tempRole = "ADMIN";
        }
        final String selectedRole = tempRole;
        signinButton.setDisable(true);
        statusLabel.setStyle("-fx-text-fill: #64748b;");
        statusLabel.setText("Signing in...");

        new Thread(() -> {
            try {
                String jsonBody = String.format(
                        "{\"username\":\"%s\", \"password\":\"%s\"}",
                        escapeJson(username),
                        escapeJson(password)
                );
                HttpResponse<String> response = BackendClient.getInstance().post("/auth/signin", jsonBody);

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        try {
                            JSONObject user = new JSONObject(response.body());
                            String userRole = user.optString("role", "BIDDER");

                            // Nếu là ADMIN thì không cần so khớp với selectedRole, cho phép đăng nhập thẳng.
                            if (!"ADMIN".equalsIgnoreCase(userRole) && !userRole.equalsIgnoreCase(selectedRole)) {
                                String roleLabel;
                                if ("SELLER".equals(selectedRole)) {
                                    roleLabel = "Seller";
                                } else if ("ADMIN".equals(selectedRole)) {
                                    roleLabel = "Admin";
                                } else {
                                    roleLabel = "Bidder";
                                }
                                statusLabel.setText("This account is not a " + roleLabel + " account!");
                                statusLabel.setStyle("-fx-text-fill: red;");
                                signinButton.setDisable(false);
                                return;
                            }

                            SessionManager.getInstance().setUser(
                                    user.getLong("id"),
                                    user.getString("username"),
                                    user.optString("fullname", username),
                                    userRole
                            );
                        } catch (Exception e) {
                            ErrorLogger.log("Lỗi parse thông tin user khi đăng nhập: " + username, e);
                            SessionManager.getInstance().setUser(0L, username, username, selectedRole);
                        }

                        // Điều hướng theo Role
                        String finalRole = SessionManager.getInstance().getRole();
                        if ("ADMIN".equalsIgnoreCase(finalRole)) {
                            goToAdminDashboard(event);
                        } else {
                            goToDashboard(event);
                        }
                    } else {
                        statusLabel.setText(BackendClient.getCleanErrorMessage(response));
                        statusLabel.setStyle("-fx-text-fill: red;");
                        signinButton.setDisable(false);
                    }
                });
            } catch (Exception e) {
                ErrorLogger.log("Lỗi kết nối khi đăng nhập: " + username, e);
                Platform.runLater(() -> {
                    statusLabel.setText("Unable to connect to the server. Make sure the backend is running.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    signinButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void onCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onGoToSignUp(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneUtils.changeScene(stage, "/fxml/signup.fxml", "User Registration", "/style/signup.css");
        } catch (Exception e) {
            ErrorLogger.log("Lỗi chuyển đổi sang màn hình Đăng ký", e);
            e.printStackTrace();
        }
    }

    private void goToDashboard(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneUtils.changeScene(stage, "/fxml/dashboard.fxml", "Auction Web - Dashboard", "/style/dashboard.css");
            stage.setMinWidth(980);
            stage.setMinHeight(680);
            stage.setMaximized(true); // Phóng to toàn bộ cửa sổ ứng dụng
        } catch (Exception e) {
            ErrorLogger.log("Lỗi mở màn hình Dashboard", e);
            statusLabel.setText("Could not open Dashboard!");
            statusLabel.setStyle("-fx-text-fill: red;");
            signinButton.setDisable(false);
            e.printStackTrace();
        }
    }

    private void goToAdminDashboard(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneUtils.changeScene(stage, "/fxml/admin_dashboard.fxml", "Auction Web - Admin Dashboard", "/style/admin_dashboard.css");
            stage.setMinWidth(1024);
            stage.setMinHeight(700);
            stage.setMaximized(true); // Phóng to toàn bộ cửa sổ ứng dụng
        } catch (Exception e) {
            ErrorLogger.log("Lỗi mở màn hình Admin Dashboard", e);
            statusLabel.setText("Could not open Admin Dashboard!");
            statusLabel.setStyle("-fx-text-fill: red;");
            signinButton.setDisable(false);
            e.printStackTrace();
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
