package frontend.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationManager {

    /**
     * Chuyển đổi màn hình và trả về Controller của màn hình mới (để có thể truyền dữ liệu).
     */
    public static <T> T switchScene(Stage stage, String fxmlPath, String cssPath, String title, int width, int height) throws Exception {
        FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene;
        if (width > 0 && height > 0) {
            scene = new Scene(root, width, height);
        } else {
            scene = new Scene(root);
        }

        if (cssPath != null && !cssPath.trim().isEmpty()) {
            java.net.URL cssUrl = NavigationManager.class.getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
        }

        stage.setScene(scene);
        if (title != null) {
            stage.setTitle(title);
        }
        stage.show();

        return loader.getController();
    }

    /**
     * Chuyển đổi màn hình với kích thước mặc định (vừa khít nội dung).
     */
    public static <T> T switchScene(Stage stage, String fxmlPath, String cssPath, String title) throws Exception {
        return switchScene(stage, fxmlPath, cssPath, title, -1, -1);
    }
}
