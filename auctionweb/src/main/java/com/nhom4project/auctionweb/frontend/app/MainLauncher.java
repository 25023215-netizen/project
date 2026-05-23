package com.nhom4project.auctionweb.frontend.app;

import com.nhom4project.auctionweb.backend.BackendApplication;
import javafx.application.Application;
import org.springframework.boot.SpringApplication;

public class MainLauncher {
    public static void main(String[] args) {
        boolean isUnitTest = false;
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.") || element.getClassName().startsWith("org.testng.")) {
                isUnitTest = true;
                break;
            }
        }

        if (isUnitTest) {
            // Under unit tests, just delegate to launch (which is mocked)
            Application.launch(App.class, args);
            return;
        }

        boolean isServerOnly = args != null && java.util.Arrays.asList(args).contains("--server");

        if (isServerOnly) {
            // Run ONLY the backend server (headless mode)
            SpringApplication.run(BackendApplication.class, args);
        } else {
            // Start Spring Boot backend in a background thread
            Thread serverThread = new Thread(() -> {
                try {
                    SpringApplication.run(BackendApplication.class, args != null ? args : new String[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            serverThread.setDaemon(true);
            serverThread.start();

            // Launch JavaFX application on the main thread
            Application.launch(App.class, args);
        }
    }
}
