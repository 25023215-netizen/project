package frontend.signup;

import frontend.utils.BackendClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class SignupController {
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField userNameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button signUpButton;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        updateButtonState();
        fullNameField.textProperty().addListener((o, old, newVal) -> updateButtonState());
        emailField.textProperty().addListener((o, old, newVal) -> updateButtonState());
        userNameField.textProperty().addListener((o, old, newVal) -> updateButtonState());
        passwordField.textProperty().addListener((o, old, newVal) -> updateButtonState());
        confirmPasswordField.textProperty().addListener((o, old, newVal) -> updateButtonState());
    }

    private void updateButtonState() {
        String fullname = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = userNameField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        boolean isValid = !fullname.isEmpty() && email.contains("@") && username.length() >= 4 
                          && password.length() >= 8 && password.equals(confirm);
        
        signUpButton.setDisable(!isValid);
        if (!isValid && !fullname.isEmpty()) {
            if (!email.contains("@")) statusLabel.setText("Email không hợp lệ");
            else if (username.length() < 4) statusLabel.setText("Username >= 4 ký tự");
            else if (password.length() < 8) statusLabel.setText("Password >= 8 ký tự");
            else if (!password.equals(confirm)) statusLabel.setText("Mật khẩu không khớp");
        } else {
            statusLabel.setText("");
        }
    }

    @FXML
    private void onSignUp(ActionEvent event) {
        signUpButton.setDisable(true);
        statusLabel.setText("Đang đăng ký...");

        new Thread(() -> {
            try {
                JSONObject payload = new JSONObject();
                payload.put("fullname", fullNameField.getText().trim());
                payload.put("email", emailField.getText().trim());
                payload.put("username", userNameField.getText().trim());
                payload.put("password", passwordField.getText());

                HttpResponse<String> response = BackendClient.getInstance().post("/auth/signup", payload.toString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đăng ký thành công!");
                        alert.showAndWait();
                        onGoToSignIn(event);
                    } else {
                        statusLabel.setText("Lỗi: " + response.body());
                        signUpButton.setDisable(false);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Lỗi: " + e.getMessage());
                    signUpButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void onGoToSignIn(ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/signin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void onCancel(ActionEvent event) { ((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); }
}
