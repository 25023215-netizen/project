package frontend.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.net.URL;

public class DashboardController {

    @FXML private StackPane contentArea;
    @FXML private Button btnHome;
    @FXML private Button btnAuctions;
    @FXML private Button btnBidding;
    @FXML private Button btnManagement;
    @FXML private Label lblUsername;
    @FXML private Label lblBalance;

    // Lưu button đang active để quản lý style
    private Button activeButton;

    @FXML
    public void initialize() {
        activeButton = btnHome;
        // Load trang chủ mặc định
        onMenuHomeClick();
    }

    /**
     * Đổi view nội dung chính theo đường dẫn FXML.
     */
    private void changeView(String fxmlPath) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                throw new RuntimeException("Không tìm thấy: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

        } catch (Exception e) {
            System.err.println("Lỗi load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật trạng thái active cho menu button.
     */
    private void setActiveButton(Button btn) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("menu-btn-active");
        }
        activeButton = btn;
        if (btn != null && !btn.getStyleClass().contains("menu-btn-active")) {
            btn.getStyleClass().add("menu-btn-active");
        }
    }

    @FXML
    private void onMenuHomeClick() {
        setActiveButton(btnHome);
        changeView("/fxml/home.fxml");
    }

    @FXML
    private void onMenuAuctionsClick() {
        setActiveButton(btnAuctions);
        changeView("/fxml/pages/auctions.fxml");
    }

    @FXML
    private void onMenuBiddingClick() {
        setActiveButton(btnBidding);
        changeView("/fxml/bidding.fxml");
    }

    @FXML
    private void onMenuManagementClick() {
        setActiveButton(btnManagement);
        changeView("/fxml/pages/management.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            URL url = getClass().getResource("/fxml/signin.fxml");
            if (url == null) {
                throw new RuntimeException("Không tìm thấy signin.fxml");
            }

            Parent signinRoot = FXMLLoader.load(url);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(signinRoot));
            stage.setTitle("Hệ thống Đấu giá - Đăng nhập");
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}