package frontend.signup;

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
        userNameField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());

        // Lắng nghe sự thay đổi của password field
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());

        // Lắng nghe sự thay đổi của confirm password field
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());
    }

    // Logic kiểm tra validation phức tạp cho signup form
    private void updateButtonState() {
        String username = userNameField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

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
        if (password.length() < 6) {
            statusLabel.setText("Password phải có ít nhất 6 ký tự");
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
        // In thông tin đăng ký ra console (sẽ thay bằng logic backend sau)
        System.out.println("Signup: " + userNameField.getText() + " / " + passwordField.getText());

        // Đóng cửa sổ sau khi xử lý xong
        closeWindow(event);
    }

    // Xử lý sự kiện khi click button Cancel
    @FXML
    private void onCancel(ActionEvent event) {
        // Đóng cửa sổ
        closeWindow(event);
    }

    // Phương thức helper để đóng cửa sổ
    private void closeWindow(ActionEvent event) {
        // Lấy Stage từ event source
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
