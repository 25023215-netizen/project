package frontend.signin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;

public class SigninController {
    @FXML private TextField userNameField;
    @FXML private PasswordField passwordField;
    @FXML private Button signinButton;
    @FXML private Label statusLabel;

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
        if (userNameField.getText().equals("admin") && passwordField.getText().equals("123456")) {
            switchToDashboard(event);
        } else {
            statusLabel.setText("Sai tài khoản hoặc mật khẩu!");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void switchToDashboard(ActionEvent event) {
        try {
            // Lưu ý: Tên file phải khớp chính xác (dashboard.fxml)
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hệ thống Quản lý - Dashboard");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Lỗi nạp Dashboard!");
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}