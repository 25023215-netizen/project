package frontend.signin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Signin extends Application {
    // Phương thức main - điểm khởi đầu của ứng dụng JavaFX
    public static void main(String[] args) {
        launch(args); // Khởi động ứng dụng JavaFX
    }

    // Phương thức start được gọi bởi JavaFX runtime sau khi ứng dụng khởi động
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML file để tạo giao diện người dùng
        // FXML định nghĩa cấu trúc UI, controller xử lý logic
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/signin.fxml"));

        // Tạo Scene với root node từ FXML
        Scene scene = new Scene(root);

        // Áp dụng CSS để styling giao diện
        scene.getStylesheets().add(getClass().getResource("/style/signin.css").toExternalForm());

        // Cấu hình cửa sổ chính
        primaryStage.setTitle("Đăng nhập người dùng");
        primaryStage.setScene(scene);
        primaryStage.show(); // Hiển thị cửa sổ
    }
}