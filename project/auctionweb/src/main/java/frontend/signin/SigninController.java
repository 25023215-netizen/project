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
        // Hiện tại chỉ hiển thị thông báo thành công (sẽ thay bằng logic backend sau)
        statusLabel.setText("Đăng nhập thành công!");
        System.out.println("Signin: " + userNameField.getText() + " / " + passwordField.getText());

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
