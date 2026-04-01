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
import javafx.stage.Stage;
import javafx.util.Pair;
//dialog để bắt ng dùng đăng nhập
public class Signup extends Application {
    public static void main(String[] args) { launch(args); }
    @Override
    public void start(Stage primaryStage) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();//dialog trả về kiểu dữ liệu gì
        dialog.setTitle("Dang ky nguoi dung");
        dialog.setHeaderText("Sign up");
        ButtonType loginButtonType = new ButtonType("login", ButtonBar.ButtonData.OK_DONE);//khai báo buttontype
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        //tạo layout
        GridPane grid = new GridPane();
        //set kích thước layout
        grid.setHgap(10); //chiều ngang
        grid.setVgap(10);// chiều dọc
        grid.setPadding(new Insets(20, 150, 10, 10));//đường viền cách trên 20, phải 150, dưới 10, trái 10
        TextField userName = new TextField();//tạo ra ô trống để điền username
        userName.setPromptText("Username");//hiển thị dòng mờ mờ "username" để nhắc biết điền gì
        PasswordField password = new PasswordField();//tạo ra ô trống, nhưng khi nhập sẽ hiện ***** vì là mật khẩu
        password.setPromptText("Password");//hiện thị dòng "password" mờ mờ
        grid.add(new Label("Username:"), 0, 0);//tạo 1 nhãn dán Username ở cột 0, hàng 0 của grid pane, giống kiểu nhãn "username:" ở trước ô trống rồi nhập tên đăng nhập đăng ký vào
        grid.add(userName, 1, 0);//nhét ô trống nhập username vào cột 1, hàng 0, nghĩa là ngay bên phải nhãn "username:"
        grid.add(new Label("Password"), 0, 1);
        grid.add(password, 1, 1);//tương tự username
        //tạo mặc định khi nào nhập username thì button"login" mới hiện ra
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);//Node là nút bấm vật lí, dòng code này là lấy button login, lookupbutton là để tìm kiếm nút bấm có gán nhãn rồi đổi từ nhãn sang nút bấm thực tế
        loginButton.setDisable(true);
        userName.textProperty().addListener((observable, oldValue, newValue) ->{//observable là 1 interface kiếm tra sự thay đổi của username, nó sẽ trả về 2 giá trịcũ old Value và mới new Value nếu kphai là node thì sẽ cho loginButton hiện ra
            loginButton.setDisable(newValue.trim().isEmpty());//nếu là khoảng trắng thì ẩn loginButton. trim() là xóa khoảng trắng ở đầu và cuối

        });
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType){
                return new Pair<>(userName.getText(), password.getText());
            }
            return null;
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(userNamepassword ->{
            System.out.println("UserName="+userNamepassword.getKey()+", Password="+userNamepassword.getValue());
        });
    }
}
