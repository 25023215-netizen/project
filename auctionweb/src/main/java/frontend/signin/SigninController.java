package frontend.signin;

import frontend.service.AuthService;
import frontend.utils.NavigationManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SigninController {
    @FXML private TextField userNameField;
    @FXML private PasswordField passwordField;
    @FXML private Button signinButton;
    @FXML private Label statusLabel;

    private final AuthService authService = new AuthService();

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
        signinButton.setDisable(true);
        statusLabel.setStyle("-fx-text-fill: #64748b;");
        statusLabel.setText("Dang dang nhap...");

        new Thread(() -> {
            AuthService.AuthResult result = authService.login(username, password);

            Platform.runLater(() -> {
                if (result.success) {
                    goToDashboard(event);
                } else {
                    statusLabel.setText(result.message);
                    statusLabel.setStyle("-fx-text-fill: red;");
                    signinButton.setDisable(false);
                }
            });
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
            NavigationManager.switchScene(stage, "/fxml/signup.fxml", "/style/signup.css", "Dang ky nguoi dung");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToDashboard(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            NavigationManager.switchScene(stage, "/fxml/dashboard.fxml", "/style/dashboard.css", "Auction Web - Dashboard", 1180, 760);
        } catch (Exception e) {
            statusLabel.setText("Khong the mo Dashboard!");
            statusLabel.setStyle("-fx-text-fill: red;");
            signinButton.setDisable(false);
            e.printStackTrace();
        }
    }
}
