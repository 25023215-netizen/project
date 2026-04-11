package frontend.dashboard;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnSignup;

    @FXML
    public void initialize() {
        setupMenuButtons();
        // Mặc định nạp trang chủ khi vừa đăng nhập
        onMenuHomeClick();
    }

    private void setupMenuButtons() {
        try {
            FontAwesomeIconView homeIcon = new FontAwesomeIconView(FontAwesomeIcon.HOME);
            homeIcon.setSize("1.6em");
            homeIcon.setStyle("-fx-fill: white;");
            btnHome.setGraphic(homeIcon);

            FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USERS); // Đổi icon sang USERS (nhóm người)
            userIcon.setSize("1.6em");
            userIcon.setStyle("-fx-fill: white;");
            btnSignup.setGraphic(userIcon);
        } catch (Exception e) {
            System.err.println("Lưu ý: Kiểm tra thư viện FontAwesome (fontawesomefx) trong dự án.");
        }
    }

    private void changeView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            System.err.println("Lỗi nạp file FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    @FXML
    private void onMenuSignupClick() {
        // Sửa đường dẫn để nạp trang Quản lý (management) thay vì trang Signup lẻ
        changeView("/fxml/pages/management.fxml");
    }

    @FXML
    private void onMenuHomeClick() {
        changeView("/fxml/home.fxml"); 
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent signinRoot = FXMLLoader.load(getClass().getResource("/fxml/signin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(signinRoot));
            stage.setTitle("Đăng nhập");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}