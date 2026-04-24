package frontend.signin;

import frontend.utils.BackendClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class SigninController {
    @FXML private TextField userNameField;
    @FXML private PasswordField passwordField;
    @FXML private Button signinButton;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        updateButtonState();
        userNameField.textProperty().addListener((o, old, newVal) -> updateButtonState());
        passwordField.textProperty().addListener((o, old, newVal) -> updateButtonState());
    }

    private void updateButtonState() {
        signinButton.setDisable(userNameField.getText().trim().isEmpty() || passwordField.getText().isEmpty());
    }

    @FXML
    private void onSignIn(ActionEvent event) {
        String username = userNameField.getText();
        String password = passwordField.getText();
        statusLabel.setText("Đang đăng nhập...");

        new Thread(() -> {
            try {
                JSONObject loginData = new JSONObject();
                loginData.put("username", username);
                loginData.put("password", password);

                HttpResponse<String> response = BackendClient.getInstance().post("/auth/signin", loginData.toString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        statusLabel.setText("Đăng nhập thành công!");
                        goToDashboard(event);
                    } else {
                        statusLabel.setText("Lỗi: " + response.body());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Lỗi kết nối: " + e.getMessage()));
            }
        }).start();
    }

    private void goToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Online Auction Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void onGoToSignUp(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng ký người dùng");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
