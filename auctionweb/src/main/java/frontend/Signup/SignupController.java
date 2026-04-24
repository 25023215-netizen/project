package frontend.Signup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class SignupController {
    // FXML injection - các field này được tự động gán từ FXML
    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField userNameField; // Input field cho username

    @FXML
    private PasswordField passwordField; // Input field cho password

    @FXML
    private PasswordField confirmPasswordField; // Input field để xác nhận password

    @FXML
    private Button signUpButton; // Button Sign Up - disabled khi validation fail

    @FXML
    private Label statusLabel; // Label hiển thị thông báo lỗi hoặc thành công

    // Phương thức được gọi sau khi FXML được load hoàn toàn
    @FXML
    public void initialize() {
        updateButtonState(); // Cập nhật trạng thái button ban đầu

        // Lắng nghe sự thay đổi của username field
        if (fullNameField != null) fullNameField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());
        if (emailField != null) emailField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());
        if (userNameField != null) userNameField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());

        // Lắng nghe sự thay đổi của password field
        if (passwordField != null) passwordField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());

        // Lắng nghe sự thay đổi của confirm password field
        if (confirmPasswordField != null) confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());
    }

    // Logic kiểm tra validation phức tạp cho signup form
    private void updateButtonState() {
        String fullname = fullNameField != null ? fullNameField.getText().trim() : "";
        String email = emailField != null ? emailField.getText().trim() : "";
        String username = userNameField != null ? userNameField.getText().trim() : "";
        String password = passwordField != null ? passwordField.getText() : "";
        String confirm = confirmPasswordField != null ? confirmPasswordField.getText() : "";

        if (fullname.isEmpty() || email.isEmpty()) {
            statusLabel.setText("Vui lòng điền đầy đủ thông tin");
            signUpButton.setDisable(true);
            return;
        }

        if (!email.contains("@")) {
            statusLabel.setText("Email không hợp lệ");
            signUpButton.setDisable(true);
            return;
        }

        // Kiểm tra username không rỗng
        if (username.isEmpty()) {
            statusLabel.setText("Username không được để trống");
            signUpButton.setDisable(true);
            return;
        }

        // Kiểm tra độ dài username tối thiểu
        if (username.length() < 4) {
            statusLabel.setText("Username phải có ít nhất 4 ký tự");
            signUpButton.setDisable(true);
            return;
        }

        // Kiểm tra password và confirm password không rỗng
        if (password.isEmpty() || confirm.isEmpty()) {
            statusLabel.setText("Password và Confirm Password không được để trống");
            signUpButton.setDisable(true);
            return;
        }

        // Kiểm tra độ dài password tối thiểu
        if (password.length() < 8) {
            statusLabel.setText("Password phải có ít nhất 8 ký tự");
            signUpButton.setDisable(true);
            return;
        }

        // Kiểm tra password và confirm password khớp nhau
        if (!password.equals(confirm)) {
            statusLabel.setText("Mật khẩu không khớp");
            signUpButton.setDisable(true);
            return;
        }

        // Tất cả validation pass - enable button và xóa thông báo lỗi
        statusLabel.setText("");
        signUpButton.setDisable(false);
    }

    // Xử lý sự kiện khi click button Sign Up
    @FXML
    private void onSignUp(ActionEvent event) {
        String fullname = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = userNameField.getText().trim();
        String password = passwordField.getText();

        signUpButton.setDisable(true);
        statusLabel.setText("Đang xử lý...");

        // Create JSON payload
        String jsonPayload = String.format(
            "{\"username\":\"%s\", \"password\":\"%s\", \"fullname\":\"%s\", \"email\":\"%s\"}",
            username.replace("\"", "\\\""),
            password.replace("\"", "\\\""),
            fullname.replace("\"", "\\\""),
            email.replace("\"", "\\\"")
        );

        // Gọi backend qua Singleton BackendClient
        try {
            java.net.http.HttpResponse<String> response = frontend.utils.BackendClient.getInstance().post("/auth/signup", jsonPayload);
            
            javafx.application.Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    alert.setTitle("Đăng ký thành công");
                    alert.setHeaderText(null);
                    alert.setContentText("Tài khoản của bạn đã được đăng ký thành công!");
                    alert.showAndWait();
                    closeWindow(event);
                } else {
                    statusLabel.setText("Lỗi: " + response.body());
                    signUpButton.setDisable(false);
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    alert.setTitle("Đăng ký thất bại");
                    alert.setHeaderText(null);
                    alert.setContentText(response.body());
                    alert.showAndWait();
                }
            });
        } catch (Exception e) {
            javafx.application.Platform.runLater(() -> {
                statusLabel.setText("Lỗi kết nối tới Server");
                signUpButton.setDisable(false);
            });
            e.printStackTrace();
        }
    }

    // Xử lý sự kiện khi click button Cancel
    @FXML
    private void onCancel(ActionEvent event) {
        // Đóng cửa sổ
        closeWindow(event);
    }

    // Xử lý sự kiện chuyển sang trang Đăng nhập
    @FXML
    private void onGoToSignIn(ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/signin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style/signin.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Đăng nhập người dùng");
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
