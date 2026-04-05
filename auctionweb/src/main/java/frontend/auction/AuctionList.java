package frontend.auction;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AuctionList extends Application {
    // Phương thức main - điểm khởi đầu của ứng dụng JavaFX
    public static void main(String[] args) {
        launch(args); // Khởi động ứng dụng JavaFX
    }

    // Phương thức start được gọi bởi JavaFX runtime sau khi ứng dụng khởi động
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML file để tạo giao diện người dùng
        // FXML định nghĩa cấu trúc UI, controller xử lý logic
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/AuctionList.fxml"));

        // Tạo Scene với root node từ FXML
        Scene scene = new Scene(root);

        // Cấu hình cửa sổ chính
        primaryStage.setTitle("Danh sách đấu giá");
        primaryStage.setScene(scene);
        primaryStage.show(); // Hiển thị cửa sổ
    }
}