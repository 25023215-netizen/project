package frontend.signin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class SigninController {
    @FXML private TextField userNameField;
    @FXML private PasswordField passwordField;
    @FXML private Button signinButton;
    @FXML private Label statusLabel;
    @FXML private Hyperlink signupLink;

    @FXML
    public void initialize() {
        signinButton.setDisable(true);
        userNameField.textProperty().addListener((o, oldV, newV) -> updateButtonState());
        passwordField.textProperty().addListener((o, oldV, newV) -> updateButtonState());
    }

    private void updateButtonState() {
        boolean disable = userNameField.getText().trim().isEmpty() || passwordField.getText().isEmpty();
        signinButton.setDisable(disable);
    }

    @FXML
    private void onSignIn(ActionEvent event) {
        String username = userNameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.equals("admin") && password.equals("123456")) {
            switchToDashboard(event);
        } else {
            statusLabel.setText("⚠️ Sai tài khoản hoặc mật khẩu!");
            statusLabel.setStyle("-fx-text-fill: #dc2626;");
        }
    }

    private void switchToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hệ thống Đấu giá - Dashboard");
            stage.setMaximized(true);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            statusLabel.setText("Lỗi nạp Dashboard! " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onGoToSignup(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
            Stage stage = (Stage) signupLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hệ thống Đấu giá - Đăng ký");
            stage.centerOnScreen();
        } catch (Exception e) {
            statusLabel.setText("Lỗi nạp trang đăng ký!");
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}