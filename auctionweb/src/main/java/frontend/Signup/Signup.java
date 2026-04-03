package frontend.signup;

import java.util.Optional;

import javax.swing.text.PasswordView;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;
//dialog để bắt ng dùng đăng nhập
public class Signup extends Application {
    public static void main(String[] args) { launch(args); }
    @Override
    public void start(Stage primaryStage) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();//dialog trả về kiểu dữ liệu gì
        dialog.setTitle("Đăng ký người dùng");
        dialog.setHeaderText("Sign up");
        ButtonType signUpButtonType = new ButtonType("Sign Up", ButtonBar.ButtonData.OK_DONE);//khai báo buttontype
        dialog.getDialogPane().getButtonTypes().addAll(signUpButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style/signup.css").toExternalForm());//gọi file css để trang trí cho dialog
        dialog.getDialogPane().getStyleClass().add("signup-dialog-pane");

        //tạo layout
        GridPane grid = new GridPane();
        //set kích thước layout
        grid.setHgap(10); //chiều ngang
        grid.setVgap(10);// chiều dọc
        grid.setPadding(new Insets(20)); // khoảng cách đều
        grid.setStyle("-fx-background-color: #f4f7fb"); //tạo màu nền xám nhạt cho layout diaglog
        TextField userName = new TextField();//tạo ra ô trống để điền username
        userName.setPromptText("Username");//hiển thị dòng mờ mờ "username" để nhắc biết điền gì
        userName.setPrefWidth(280);//ô nhập username có chiều rộng 280 pixel
        PasswordField password = new PasswordField();//tạo ra ô trống, nhưng khi nhập sẽ hiện ***** vì là mật khẩu
        password.setPromptText("Password");//hiện thị dòng "password" mờ mờ
        password.setPrefWidth(280);
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm Password");
        confirmPassword.setPrefWidth(280);

        Label statusLabel = new Label();//tạo thông báo khi nhập ko hợp lệ
        statusLabel.setStyle("-fx-text-fill: #d00000; -fx-font-weight: bold;");//đặt màu đỏ cho thông báo lỗi và in đậm
        grid.add(new Label("Username"), 0, 0);//tạo 1 nhãn dán Username ở cột 0, hàng 0 của grid pane, giống kiểu nhãn "username:" ở trước ô trống rồi nhập tên đăng nhập đăng ký vào
        grid.add(userName, 1, 0);//nhét ô trống nhập username vào cột 1, hàng 0, nghĩa là ngay bên phải nhãn "username:"
        grid.add(new Label("Password"), 0, 1);
        grid.add(password, 1, 1);//tương tự username
        grid.add(new Label("Confirm Password"), 0, 2);
        grid.add(confirmPassword, 1, 2);
        grid.add(statusLabel, 1, 3);

        //tạo mặc định khi nào nhập username thì button"Sign Up" mới hiện ra
        Node signupButton = dialog.getDialogPane().lookupButton(signUpButtonType);//Node là nút bấm vật lí, dòng code này là lấy button login, lookupbutton là để tìm kiếm nút bấm có gán nhãn rồi đổi từ nhãn sang nút bấm thực tế
        signupButton.setDisable(true);
        Runnable validateInput = () -> {
            boolean isUsernameEmpty = userName.getText().trim().isEmpty();//getText để lấy nội dung đã nhập, trim để loại bỏ khoảng trắng ở đầu và cuối, isEmpty để kiểm tra có phải chỉ nhập dấu cách ko
            boolean isUsernameTooShort = userName.getText().trim().length() < 4;
            boolean isPasswordEmpty = password.getText().isEmpty();
            boolean isPasswordTooShort = password.getText().length() < 6;
            boolean isConfirmEmpty = confirmPassword.getText().isEmpty();
            boolean isPasswordMatch = password.getText().equals(confirmPassword.getText());//password và confirm password phải giống nhau   

            if (isUsernameEmpty) {
                statusLabel.setText("Username không được để trống");
            } else if (isUsernameTooShort) {
                statusLabel.setText("Username phải có ít nhất 4 ký tự");
            } else if (isPasswordEmpty || isConfirmEmpty) {//password hoặc confirm ko đc bỏ trống
                statusLabel.setText("Password và Confirm Password không được để trống");
            } else if (isPasswordTooShort) {
                statusLabel.setText("Password phải có ít nhất 6 ký tự");
            } else if (!isPasswordMatch) {
                statusLabel.setText("Mật khẩu không khớp");
            } else {
                statusLabel.setText("");
            }

            signupButton.setDisable(isUsernameEmpty || isUsernameTooShort || isPasswordEmpty || isPasswordTooShort || !isPasswordMatch);
        };
        userName.textProperty().addListener((observable, oldValue, newValue) -> validateInput.run());//addListener để theo dõi sự thay đổi của bất kỳ ô nào thì hệ thống sẽ nhận biết và chạy kiểm tra lại từ đầu, nếu có lỗi thì hiện thông báo lỗi, nếu ko có lỗi thì ẩn thông báo lỗi đi và bật nút "sign up"
        password.textProperty().addListener((observable, oldValue, newValue) -> validateInput.run());//validateInput để kiểm tra điều kiện bật nút "Sign Up"
        confirmPassword.textProperty().addListener((observable, oldValue, newValue) -> validateInput.run());

        dialog.getDialogPane().setContent(grid);//nhét lauout để tạo vào diaglog
        dialog.setResultConverter(dialogButton -> {//trả về kết quả khi người dùng bấm nút
            if (dialogButton == signUpButtonType){//nếu là nút "sign up" thì trả về cặp username và password về cho hệ thống
                return new Pair<>(userName.getText(), password.getText());//nếu là cancel thì không trả gì cả
            }
            return null;
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();//showAndWait để hiển thị dialog và chờ người dùng tương tác thì mới đc chạy tiếp, optional là tùy vào người dùng, nếu người dùng bấm "sign up" thì trả về cặp chuỗi username và password, nếu cancel trả về null
        result.ifPresent(userNamepassword ->{
            System.out.println("UserName="+userNamepassword.getKey()+", Password="+userNamepassword.getValue());
        });//nếu người dùng "sign up" thì ifPresent sẽ kiểm tra có giá trị hay ko, nếu có sẽ in ra username và password đã nhập về hệ thống 
    }
}
