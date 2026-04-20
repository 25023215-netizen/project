package frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        URL fxml = getClass().getResource("/fxml/signin.fxml");

        if (fxml == null) {
            throw new RuntimeException("Không tìm thấy signin.fxml!");
        }

        Parent root = FXMLLoader.load(fxml);

        Scene scene = new Scene(root, 520, 600);
        primaryStage.setTitle("Hệ thống Đấu giá - Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}