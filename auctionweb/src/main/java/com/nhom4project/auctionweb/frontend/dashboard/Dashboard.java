package com.nhom4project.auctionweb.frontend.dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.nhom4project.auctionweb.frontend.utils.WindowUtil;

public class Dashboard extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
        Scene scene = new Scene(root, 1180, 760);
        scene.getStylesheets().add(getClass().getResource("/style/dashboard.css").toExternalForm());
        primaryStage.setTitle("Auction Web - Dashboard");
        primaryStage.setScene(scene);
        WindowUtil.fitDashboard(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}




