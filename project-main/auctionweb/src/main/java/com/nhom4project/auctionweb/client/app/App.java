package com.nhom4project.auctionweb.client.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxmlLocation = getClass().getResource("/fxml/signin.fxml");
            if (fxmlLocation == null) {
                System.err.println("Error: Could not find signin.fxml");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 500, 600);
            URL cssLocation = getClass().getResource("/style/signin.css");
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            }
            
            primaryStage.setTitle("Hệ Thống Đấu Giá Trực Tuyến - Đăng Nhập");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(480);
            primaryStage.setMinHeight(580);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Critical Error starting application: " + e.getMessage());
        }
    }
}




