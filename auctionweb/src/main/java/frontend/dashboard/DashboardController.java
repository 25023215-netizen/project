// DashboardController.java
package frontend.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane contentArea; // Đây là khu vực trống ở giữa

    // Phương thức để load file FXML bất kỳ vào vùng trống
    private void changeView(String fxmlPath) {
        try {
            // Load file fxml mới
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            
            // Xóa nội dung cũ và thêm nội dung mới vào khung
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onMenuSignupClick() {
        // Khi bấm nút "Quản lý" hoặc "Đăng ký" trên Menu, nạp Signup.fxml vào giữa
        changeView("/fxml/signup.fxml");
    }
    
    @FXML
    private void onMenuHomeClick() {
        // Nạp trang chủ hoặc nội dung khác
        changeView("/fxml/home.fxml");
    }
}