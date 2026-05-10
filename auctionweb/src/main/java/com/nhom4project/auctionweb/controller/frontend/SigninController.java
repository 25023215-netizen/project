package com.nhom4project.auctionweb.controller.frontend;

import com.nhom4project.auctionweb.client.utils.BackendClient;
import com.nhom4project.auctionweb.client.utils.SessionManager;
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
        String selectedRole = roleComboBox.getValue().toUpperCase();
        signinButton.setDisable(true);
        statusLabel.setStyle("-fx-text-fill: #64748b;");
        statusLabel.setText("Dang dang nhap...");

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

                            // Kiểm tra role đã chọn có khớp với role trong DB không
                            if (!userRole.equalsIgnoreCase(selectedRole)) {
                                statusLabel.setText("Tai khoan nay khong phai " + selectedRole + "!");
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
                            SessionManager.getInstance().setUser(0L, username, username, selectedRole);
                        }

                        // Điều hướng theo Role
                        if ("ADMIN".equals(selectedRole)) {
                            goToAdminDashboard(event);
                        } else {
                            goToDashboard(event);
                        }
                    } else {
                        statusLabel.setText("Loi: " + response.body());
                        statusLabel.setStyle("-fx-text-fill: red;");
                        signinButton.setDisable(false);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Khong the ket noi toi may chu!");
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
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style/signup.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Dang ky nguoi dung");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1180, 760);
            scene.getStylesheets().add(getClass().getResource("/style/dashboard.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Auction Web - Dashboard");
            stage.setMinWidth(980);
            stage.setMinHeight(680);
            stage.show();
        } catch (Exception e) {
            statusLabel.setText("Khong the mo Dashboard!");
            statusLabel.setStyle("-fx-text-fill: red;");
            signinButton.setDisable(false);
            e.printStackTrace();
        }
    }

    private void goToAdminDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/admin_dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            scene.getStylesheets().add(getClass().getResource("/style/admin_dashboard.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Auction Web - Admin Dashboard");
            stage.setMinWidth(1024);
            stage.setMinHeight(700);
            stage.show();
        } catch (Exception e) {
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
