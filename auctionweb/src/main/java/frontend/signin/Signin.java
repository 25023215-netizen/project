package frontend.signin;

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
public class Signin extends Application {
    public static void main(String[] args) { launch(args); }
    @Override
    public void start(Stage primaryStage) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();//dialog trả về kiểu dữ liệu gì
        dialog.setTitle("Đăng nhập người dùng");
        dialog.setHeaderText("Sign in");
        ButtonType signinButtonType = new ButtonType("Sign in", ButtonBar.ButtonData.OK_DONE);//khai báo buttontype
        dialog.getDialogPane().getButtonTypes().addAll(signinButtonType, ButtonType.CANCEL);

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
        

        Label statusLabel = new Label();//tạo thông báo khi nhập ko hợp lệ
        statusLabel.setStyle("-fx-text-fill: #d00000; -fx-font-weight: bold;");//đặt màu đỏ cho thông báo lỗi và in đậm
        grid.add(new Label("Username:"), 0, 0);//tạo 1 nhãn dán Username ở cột 0, hàng 0 của grid pane, giống kiểu nhãn "username:" ở trước ô trống rồi nhập tên đăng nhập đăng ký vào
        grid.add(userName, 1, 0);//nhét ô trống nhập username vào cột 1, hàng 0, nghĩa là ngay bên phải nhãn "username:"
        grid.add(new Label("Password"), 0, 1);
        grid.add(password, 1, 1);//tương tự username
        grid.add(statusLabel, 1, 3);

        //tạo mặc định khi nào nhập username thì button"Sign Up" mới hiện ra
        Node signinButton = dialog.getDialogPane().lookupButton(signinButtonType);//Node là nút bấm vật lí, dòng code này là lấy button login, lookupbutton là để tìm kiếm nút bấm có gán nhãn rồi đổi từ nhãn sang nút bấm thực tế
        signinButton.setDisable(true); 

        
        Optional<Pair<String, String>> result = dialog.showAndWait();//showAndWait để hiển thị dialog và chờ người dùng tương tác thì mới đc chạy tiếp, optional là tùy vào người dùng, nếu người dùng bấm "sign up" thì trả về cặp chuỗi username và password, nếu cancel trả về null
        result.ifPresent(userNamepassword ->{
            System.out.println("UserName="+userNamepassword.getKey()+", Password="+userNamepassword.getValue());
        });//nếu người dùng "sign up" thì ifPresent sẽ kiểm tra có giá trị hay ko, nếu có sẽ in ra username và password đã nhập về hệ thống 
    }
}
