package com.nhom4project.auctionweb.client.utils;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SceneUtils.
 * Uses Mockito mockConstruction for FXMLLoader and Scene to enable full isolation
 * from physical JavaFX platform constraints.
 */
public class SceneUtilsTest {

    /**
     * TC_SU_01: changeScene when no scene exists on the stage.
     * Expected:
     * - A new Scene is constructed.
     * - stage.setScene() is called.
     * - Stylesheet is registered.
     */
    @Test
    public void testChangeSceneWhenNoSceneExists() {
        Stage mockStage = mock(Stage.class);
        when(mockStage.getScene()).thenReturn(null); // No scene present

        Parent mockParent = mock(Parent.class);
        ObservableList<String> mockStylesheets = mock(ObservableList.class);

        try (MockedConstruction<FXMLLoader> mockLoader = mockConstruction(FXMLLoader.class,
                (mock, context) -> {
                    when(mock.load()).thenReturn(mockParent);
                });
             MockedConstruction<Scene> mockScene = mockConstruction(Scene.class,
                     (mock, context) -> {
                         when(mock.getStylesheets()).thenReturn(mockStylesheets);
                     })) {

            assertDoesNotThrow(() -> SceneUtils.changeScene(
                    mockStage,
                    "/fxml/signin.fxml",
                    "Sign In Title",
                    "/style/signin.css"
            ));

            verify(mockStage).setScene(any(Scene.class));
            verify(mockStage).setTitle("Sign In Title");
            verify(mockStage).show();
            verify(mockStylesheets).clear();
            verify(mockStylesheets).add(contains("signin.css"));
        }
    }

    /**
     * TC_SU_02: changeScene when an active scene already exists on the stage.
     * Expected:
     * - The existing scene is reused.
     * - scene.setRoot() is called to preserve dimensions.
     * - stage.setScene() is NOT called again.
     */
    @Test
    public void testChangeSceneWhenSceneExists() {
        Stage mockStage = mock(Stage.class);
        Scene mockSceneObj = mock(Scene.class);
        when(mockStage.getScene()).thenReturn(mockSceneObj); // Existing scene present

        Parent mockParent = mock(Parent.class);
        ObservableList<String> mockStylesheets = mock(ObservableList.class);
        when(mockSceneObj.getStylesheets()).thenReturn(mockStylesheets);

        try (MockedConstruction<FXMLLoader> mockLoader = mockConstruction(FXMLLoader.class,
                (mock, context) -> {
                    when(mock.load()).thenReturn(mockParent);
                })) {

            assertDoesNotThrow(() -> SceneUtils.changeScene(
                    mockStage,
                    "/fxml/signin.fxml",
                    "Sign In Title",
                    "/style/signin.css"
            ));

            // Should update root on current scene and NOT create/set a new scene
            verify(mockSceneObj).setRoot(mockParent);
            verify(mockStage, never()).setScene(any(Scene.class));
            verify(mockStage).setTitle("Sign In Title");
            verify(mockStage).show();
            verify(mockStylesheets).clear();
            verify(mockStylesheets).add(contains("signin.css"));
        }
    }

    /**
     * TC_SU_03: changeSceneWithController happy path.
     * Expected:
     * - Triggers transition successfully.
     * - Returns the mocked controller instance from the FXMLLoader.
     */
    @Test
    public void testChangeSceneWithController() {
        Stage mockStage = mock(Stage.class);
        when(mockStage.getScene()).thenReturn(null);

        Parent mockParent = mock(Parent.class);
        ObservableList<String> mockStylesheets = mock(ObservableList.class);
        Object mockController = new Object();

        try (MockedConstruction<FXMLLoader> mockLoader = mockConstruction(FXMLLoader.class,
                (mock, context) -> {
                    when(mock.load()).thenReturn(mockParent);
                    when(mock.getController()).thenReturn(mockController);
                });
             MockedConstruction<Scene> mockScene = mockConstruction(Scene.class,
                     (mock, context) -> {
                         when(mock.getStylesheets()).thenReturn(mockStylesheets);
                     })) {

            Object controller = SceneUtils.changeSceneWithController(
                    mockStage,
                    "/fxml/signin.fxml",
                    "Sign In Title",
                    null
            );

            assertNotNull(controller);
            assertSame(mockController, controller);
            verify(mockStage).setScene(any(Scene.class));
            verify(mockStage).setTitle("Sign In Title");
            verify(mockStage).show();
        }
    }
}
