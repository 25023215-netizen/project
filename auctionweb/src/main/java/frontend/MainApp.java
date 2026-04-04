package frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Luôn bắt đầu từ màn hình Signin
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/signin.fxml"));
        primaryStage.setTitle("Hệ thống quản lý - Đăng nhập");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}