package com.nhom4project.auctionweb.frontend.util;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.util.List;

public final class WindowUtil {
    private static final double DASHBOARD_WIDTH = 1180;
    private static final double DASHBOARD_HEIGHT = 760;
    private static final double DASHBOARD_MIN_WIDTH = 900;
    private static final double DASHBOARD_MIN_HEIGHT = 640;

    private WindowUtil() {
    }

    private static Rectangle2D getScreenBounds(Stage stage) {
        if (stage != null && stage.isShowing()) {
            try {
                List<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
                if (!screens.isEmpty()) {
                    return screens.get(0).getVisualBounds();
                }
            } catch (Exception ignored) {
                // Fall back to primary screen
            }
        }
        return Screen.getPrimary().getVisualBounds();
    }

    public static void fitDashboard(Stage stage) {
        Rectangle2D bounds = getScreenBounds(stage);
        double width = Math.min(DASHBOARD_WIDTH, bounds.getWidth());
        double height = Math.min(DASHBOARD_HEIGHT, bounds.getHeight());

        stage.setMinWidth(Math.min(DASHBOARD_MIN_WIDTH, bounds.getWidth()));
        stage.setMinHeight(Math.min(DASHBOARD_MIN_HEIGHT, bounds.getHeight()));
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(bounds.getMinX() + (bounds.getWidth() - width) / 2);
        stage.setY(bounds.getMinY() + (bounds.getHeight() - height) / 2);
    }

    public static void maximizeStage(Stage stage) {
        if (stage == null) {
            return;
        }

        Platform.runLater(() -> {
            Rectangle2D bounds = getScreenBounds(stage);
            stage.setMaximized(false);
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            stage.setMaximized(true);
        });
    }
}
