package com.nhom4project.auctionweb.frontend.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility class to handle scene switching in JavaFX.
 * Ensures the window size and state (maximized) are preserved.
 */
public class SceneUtils {

    public static void changeScene(Stage stage, String fxmlPath, String title, String stylesheetPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(fxmlPath));
            Parent root = loader.load();

            boolean wasMaximized = stage.isMaximized();
            boolean wasFullScreen = stage.isFullScreen();
            double width = stage.getWidth() > 0 ? stage.getWidth() : 1180;
            double height = stage.getHeight() > 0 ? stage.getHeight() : 760;

            Scene currentScene = stage.getScene();
            if (currentScene == null) {
                currentScene = new Scene(root, width, height);
                stage.setScene(currentScene);
            } else {
                currentScene.setRoot(root);
            }

            if (stylesheetPath != null) {
                currentScene.getStylesheets().clear();
                currentScene.getStylesheets().add(SceneUtils.class.getResource(stylesheetPath).toExternalForm());
            }

            stage.setTitle(title);
            if (!stage.isShowing()) {
                stage.show();
            }

            javafx.application.Platform.runLater(() -> {
                if (wasMaximized) {
                    stage.setMaximized(true);
                }
                if (wasFullScreen) {
                    stage.setFullScreen(true);
                }
            });
        } catch (Exception e) {
            System.err.println("Error changing scene to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Specialized version for opening auction details which requires passing data to the controller.
     */
    public static <T> T changeSceneWithController(Stage stage, String fxmlPath, String title, String stylesheetPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(fxmlPath));
            Parent root = loader.load();

            boolean wasMaximized = stage.isMaximized();
            boolean wasFullScreen = stage.isFullScreen();
            double width = stage.getWidth() > 0 ? stage.getWidth() : 1180;
            double height = stage.getHeight() > 0 ? stage.getHeight() : 760;

            Scene currentScene = stage.getScene();
            if (currentScene == null) {
                currentScene = new Scene(root, width, height);
                stage.setScene(currentScene);
            } else {
                currentScene.setRoot(root);
            }

            if (stylesheetPath != null) {
                currentScene.getStylesheets().clear();
                currentScene.getStylesheets().add(SceneUtils.class.getResource(stylesheetPath).toExternalForm());
            }

            stage.setTitle(title);
            if (!stage.isShowing()) {
                stage.show();
            }

            javafx.application.Platform.runLater(() -> {
                if (wasMaximized) {
                    stage.setMaximized(true);
                }
                if (wasFullScreen) {
                    stage.setFullScreen(true);
                }
            });

            return loader.getController();
        } catch (Exception e) {
            System.err.println("Error changing scene with controller to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
