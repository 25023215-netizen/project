package frontend.signup;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class SignupController {
    @FXML private TextField userNameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button signUpButton;
    @FXML private Label statusLabel;
    @FXML private Hyperlink loginLink;

    @FXML
    public void initialize() {
        updateButtonState();
        userNameField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());
    }

    private void updateButtonState() {
        String username = userNameField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isEmpty()) {
            statusLabel.setText("");
            signUpButton.setDisable(true);
            return;
        }

        if (username.length() < 4) {
            statusLabel.setText("⚠️ Tên đăng nhập phải có ít nhất 4 ký tự");
            statusLabel.setStyle("-fx-text-fill: #f59e0b;");
            signUpButton.setDisable(true);
            return;
        }

        if (password.isEmpty() || confirm.isEmpty()) {
            statusLabel.setText("");
            signUpButton.setDisable(true);
            return;
        }

        if (password.length() < 6) {
            statusLabel.setText("⚠️ Mật khẩu phải có ít nhất 6 ký tự");
            statusLabel.setStyle("-fx-text-fill: #f59e0b;");
            signUpButton.setDisable(true);
            return;
        }

        if (!password.equals(confirm)) {
            statusLabel.setText("❌ Mật khẩu không khớp");
            statusLabel.setStyle("-fx-text-fill: #dc2626;");
            signUpButton.setDisable(true);
            return;
        }

        statusLabel.setText("✅ Sẵn sàng đăng ký");
        statusLabel.setStyle("-fx-text-fill: #16a34a;");
        signUpButton.setDisable(false);
    }

    @FXML
    private void onSignUp(ActionEvent event) {
        System.out.println("Signup: " + userNameField.getText() + " / " + passwordField.getText());
        statusLabel.setText("✅ Đăng ký thành công! Đang chuyển đến đăng nhập...");
        statusLabel.setStyle("-fx-text-fill: #16a34a;");

        // Chuyển về trang đăng nhập sau khi đăng ký thành công
        javafx.application.Platform.runLater(() -> {
            try {
                Thread.sleep(1000);
                onGoToLogin(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void onGoToLogin(ActionEvent event) {
        try {
            Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/signin.fxml"));
            Stage stage;
            if (loginLink != null) {
                stage = (Stage) loginLink.getScene().getWindow();
            } else {
                stage = (Stage) signUpButton.getScene().getWindow();
            }
            stage.setScene(new Scene(root));
            stage.setTitle("Hệ thống Đấu giá - Đăng nhập");
            stage.centerOnScreen();
        } catch (Exception e) {
            statusLabel.setText("Lỗi nạp trang đăng nhập!");
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        // Xóa trắng form khi nhấn Hủy
        userNameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        statusLabel.setText("Đã hủy thao tác.");
        statusLabel.setStyle("-fx-text-fill: #f59e0b;");
    }
}