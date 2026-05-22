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
            
            Scene currentScene = stage.getScene();
            if (currentScene == null) {
                // If no scene exists, create one with default size
                currentScene = new Scene(root, 1180, 760);
                stage.setScene(currentScene);
            } else {
                // If scene exists, just update the root to preserve window size/state
                currentScene.setRoot(root);
            }
            
            // Update stylesheets
            if (stylesheetPath != null) {
                currentScene.getStylesheets().clear();
                currentScene.getStylesheets().add(SceneUtils.class.getResource(stylesheetPath).toExternalForm());
            }
            
            stage.setTitle(title);
            if (!stage.isShowing()) {
                stage.show();
            }
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
            
            Scene currentScene = stage.getScene();
            if (currentScene == null) {
                currentScene = new Scene(root, 1180, 760);
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
            
            return loader.getController();
        } catch (Exception e) {
            System.err.println("Error changing scene with controller to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
