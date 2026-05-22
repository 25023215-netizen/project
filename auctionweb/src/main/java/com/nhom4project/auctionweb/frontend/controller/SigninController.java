package com.nhom4project.auctionweb.frontend.controller;

import com.nhom4project.auctionweb.frontend.util.BackendClient;
import com.nhom4project.auctionweb.frontend.util.SceneUtils;
import com.nhom4project.auctionweb.frontend.util.SessionManager;
import com.nhom4project.auctionweb.frontend.util.ErrorLogger;
import com.nhom4project.auctionweb.frontend.util.WindowUtil;
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
        statusLabel.setText("Đang đăng nhập...");

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
                                String roleVN = "SELLER".equals(selectedRole) ? "Người bán" : "Người đấu giá";
                                statusLabel.setText("Tài khoản này không phải " + roleVN + "!");
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

                            // Điều hướng theo role thực tế trả về từ server
                            if ("ADMIN".equalsIgnoreCase(userRole)) {
                                goToAdminDashboard(event);
                            } else {
                                goToDashboard(event);
                            }

                        } catch (Exception e) {
                            ErrorLogger.log("Lỗi parse thông tin user khi đăng nhập: " + username, e);
                            SessionManager.getInstance().setUser(0L, username, username, selectedRole);
                            // Fallback: điều hướng theo lựa chọn người dùng (nếu parse lỗi)
                            if ("ADMIN".equalsIgnoreCase(selectedRole)) {
                                goToAdminDashboard(event);
                            } else {
                                goToDashboard(event);
                            }
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
                    statusLabel.setText("Không thể kết nối tới máy chủ! Hãy chắc chắn backend đã chạy.");
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
            SceneUtils.changeScene(stage, "/fxml/signup.fxml", "Dang ky nguoi dung", "/style/signup.css");
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
            WindowUtil.maximizeStage(stage);
        } catch (Exception e) {
            ErrorLogger.log("Lỗi mở màn hình Dashboard", e);
            statusLabel.setText("Khong the mo Dashboard!");
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
            WindowUtil.maximizeStage(stage);
        } catch (Exception e) {
            ErrorLogger.log("Lỗi mở màn hình Admin Dashboard", e);
            statusLabel.setText("Khong the mo Admin Dashboard!");
            statusLabel.setStyle("-fx-text-fill: red;");
            signinButton.setDisable(false);
            e.printStackTrace();
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
