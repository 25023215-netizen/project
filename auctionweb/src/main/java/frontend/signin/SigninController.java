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
    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signinButton;

    @FXML
    private Label statusLabel;

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

    // --- PHẦN SỬA ĐỔI CHÍNH TẠI ĐÂY ---
    @FXML
    private void onSignIn(ActionEvent event) {
        // 1. Giả lập kiểm tra đăng nhập (Sau này bạn sẽ kết nối Database ở đây)
        String username = userNameField.getText();
        String password = passwordField.getText();

        if (username.equals("admin") && password.equals("123456")) {
            statusLabel.setText("Đăng nhập thành công! Đang chuyển hướng...");
            
            // 2. Gọi hàm chuyển sang màn hình Dashboard
            switchToDashboard(event);
        } else {
            statusLabel.setText("Sai tài khoản hoặc mật khẩu!");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void switchToDashboard(ActionEvent event) {
        try {
            // 3. Tải file FXML của Dashboard (cái "vỏ" chứa Menu và khu vực hiển thị động)
            // Đảm bảo đường dẫn file Dashboard.fxml chính xác trong project của bạn
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // 4. Tạo một Scene mới cho Dashboard
            Scene dashboardScene = new Scene(dashboardRoot);
            
            // 5. Lấy Stage hiện tại (cửa sổ Signin) và thay thế nội dung
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(dashboardScene);
            currentStage.setTitle("Hệ thống Quản lý - Dashboard");
            currentStage.centerOnScreen(); // Đưa cửa sổ ra giữa màn hình
            
            // Hiển thị Dashboard
            currentStage.show();

        } catch (IOException e) {
            statusLabel.setText("Lỗi: Không thể tải giao diện Dashboard.");
            e.printStackTrace();
        }
    }
    // ----------------------------------

    @FXML
    private void onCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}