package com.nhom4project.auctionweb.frontend.utils;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class WindowUtil {
    private static final double DASHBOARD_WIDTH = 1180;
    private static final double DASHBOARD_HEIGHT = 760;
    private static final double DASHBOARD_MIN_WIDTH = 900;
    private static final double DASHBOARD_MIN_HEIGHT = 640;

    private WindowUtil() {
    }

    public static void fitDashboard(Stage stage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double width = Math.min(DASHBOARD_WIDTH, bounds.getWidth());
        double height = Math.min(DASHBOARD_HEIGHT, bounds.getHeight());

        stage.setMinWidth(Math.min(DASHBOARD_MIN_WIDTH, bounds.getWidth()));
        stage.setMinHeight(Math.min(DASHBOARD_MIN_HEIGHT, bounds.getHeight()));
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(bounds.getMinX() + (bounds.getWidth() - width) / 2);
        stage.setY(bounds.getMinY() + (bounds.getHeight() - height) / 2);
    }
}
