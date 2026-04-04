package frontend.signup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class SignupController {
    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button signUpButton;

    @FXML
    private Label statusLabel;

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
            statusLabel.setText("Username không được để trống");
            signUpButton.setDisable(true);
            return;
        }

        if (username.length() < 4) {
            statusLabel.setText("Username phải có ít nhất 4 ký tự");
            signUpButton.setDisable(true);
            return;
        }

        if (password.isEmpty() || confirm.isEmpty()) {
            statusLabel.setText("Password và Confirm Password không được để trống");
            signUpButton.setDisable(true);
            return;
        }

        if (password.length() < 6) {
            statusLabel.setText("Password phải có ít nhất 6 ký tự");
            signUpButton.setDisable(true);
            return;
        }

        if (!password.equals(confirm)) {
            statusLabel.setText("Mật khẩu không khớp");
            signUpButton.setDisable(true);
            return;
        }

        statusLabel.setText("");
        signUpButton.setDisable(false);
    }

    @FXML
    private void onSignUp(ActionEvent event) {
        // Logic xử lý khi nhấn Đăng ký
        System.out.println("Signup: " + userNameField.getText() + " / " + passwordField.getText());
        statusLabel.setText("Đăng ký thành công!");
        statusLabel.setStyle("-fx-text-fill: green;");

        // Đã xóa closeWindow(event) để không làm sập Dashboard
    }

    @FXML
    private void onCancel(ActionEvent event) {
        // Xóa trắng form khi nhấn Hủy
        userNameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        statusLabel.setText("Đã hủy thao tác.");
        statusLabel.setStyle("-fx-text-fill: orange;");

        // Đã xóa closeWindow(event) để không làm sập Dashboard
    }
}