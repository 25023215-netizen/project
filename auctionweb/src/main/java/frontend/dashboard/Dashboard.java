package frontend.dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Dashboard extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
        Scene scene = new Scene(root, 1180, 760);
        scene.getStylesheets().add(getClass().getResource("/style/dashboard.css").toExternalForm());
        primaryStage.setTitle("Auction Web - Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(980);
        primaryStage.setMinHeight(680);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
