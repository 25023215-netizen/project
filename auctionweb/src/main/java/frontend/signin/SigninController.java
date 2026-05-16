package frontend.signin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class SigninController {
    // FXML injection - các field này được tự động gán từ FXML
    @FXML
    private TextField userNameField; // Input field cho username

    @FXML
    private PasswordField passwordField; // Input field cho password

    @FXML
    private Button signinButton; // Button Sign In - disabled khi input không hợp lệ

    @FXML
    private Label statusLabel; // Label hiển thị thông báo lỗi

    // Phương thức được gọi sau khi FXML được load hoàn toàn
    @FXML
    public void initialize() {
        updateButtonState(); // Cập nhật trạng thái button ban đầu

        // Lắng nghe sự thay đổi của username field
        userNameField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());

        // Lắng nghe sự thay đổi của password field
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());
    }

    // Logic kiểm tra và cập nhật trạng thái button Sign In
    private void updateButtonState() {
        // Button chỉ enable khi cả username và password đều có nội dung
        boolean disable = userNameField.getText().trim().isEmpty() || passwordField.getText().isEmpty();
        signinButton.setDisable(disable);

        // Xóa thông báo lỗi khi user đang nhập
        if (disable) {
            statusLabel.setText("");
        }
    }

    // Xử lý sự kiện khi click button Sign In
    @FXML
    private void onSignIn(ActionEvent event) {
        String username = userNameField.getText().trim();
        String password = passwordField.getText();

        try {
            // Tạo JSON body đơn giản
            String jsonBody = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
            
            // Gọi backend qua Singleton BackendClient
            java.net.http.HttpResponse<String> response = frontend.utils.BackendClient.getInstance().post("/auth/signin", jsonBody);

            if (response.statusCode() == 200) {
                statusLabel.setText("Đăng nhập thành công!");
                statusLabel.setStyle("-fx-text-fill: green;");
                System.out.println("Login success: " + response.body());
                // Chuyển màn hình hoặc lưu thông tin user ở đây
            } else {
                statusLabel.setText("Lỗi: " + response.body());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            statusLabel.setText("Không thể kết nối tới máy chủ!");
            statusLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    // Xử lý sự kiện khi click button Cancel
    @FXML
    private void onCancel(ActionEvent event) {
        // Đóng cửa sổ
        closeWindow(event);
    }

    // Xử lý sự kiện chuyển sang trang Đăng ký
    @FXML
    private void onGoToSignUp(ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style/signup.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Đăng ký người dùng");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    // Phương thức helper để đóng cửa sổ
    private void closeWindow(ActionEvent event) {
        // Lấy Stage từ event source
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
