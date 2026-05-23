package com.nhom4project.auctionweb.frontend.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.json.JSONObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Singleton quản lý phiên đăng nhập của user trên client.
 * Lưu thông tin user sau khi đăng nhập thành công.
 */
public class SessionManager {
    private static SessionManager instance;

    private Long userId;
    private String username;
    private String fullname;
    private String role; // BIDDER, SELLER, ADMIN
    private ScheduledExecutorService scheduler;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUser(Long userId, String username, String fullname, String role) {
        this.userId = userId;
        this.username = username;
        this.fullname = fullname;
        this.role = role;
        
        if (userId != null && userId > 0 && !"ADMIN".equalsIgnoreCase(role)) {
            startStatusCheck();
        }
    }

    public void clear() {
        stopStatusCheck();
        this.userId = null;
        this.username = null;
        this.fullname = null;
        this.role = null;
    }

    public void startStatusCheck() {
        stopStatusCheck();
        scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread t = new Thread(runnable, "UserStatusChecker");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(this::checkUserStatus, 0, 3, TimeUnit.SECONDS);
    }

    public void stopStatusCheck() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    private void checkUserStatus() {
        if (userId == null) return;
        try {
            java.net.http.HttpResponse<String> response = BackendClient.getInstance().get("/auth/status?userId=" + userId);
            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                boolean locked = json.optBoolean("locked", false);
                if (locked) {
                    try {
                        Platform.runLater(() -> {
                            stopStatusCheck();
                            try {
                                javafx.stage.Stage activeStage = null;
                                java.util.List<javafx.stage.Window> windows = javafx.stage.Window.getWindows();
                                for (javafx.stage.Window w : windows) {
                                    if (w instanceof javafx.stage.Stage stage && stage.isShowing()) {
                                        activeStage = stage;
                                        break;
                                    }
                                }
                                
                                if (activeStage != null) {
                                    SceneUtils.changeScene(activeStage, "/fxml/locked_account.fxml", "Tài khoản bị khóa", "/style/locked_account.css");
                                    activeStage.setMinWidth(480);
                                    activeStage.setMinHeight(380);
                                    activeStage.setWidth(500);
                                    activeStage.setHeight(400);
                                    activeStage.centerOnScreen();
                                    
                                    // Đóng tất cả các window/stage khác để hoàn toàn khóa ứng dụng
                                    for (javafx.stage.Window w : windows) {
                                        if (w instanceof javafx.stage.Stage stage && stage != activeStage) {
                                            stage.close();
                                        }
                                    }
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("Thông báo");
                                    alert.setHeaderText("Tài khoản bị khóa");
                                    alert.setContentText("Tài khoản này đã bị khoá và sẽ không thể thực hiện được hành động gì cả");
                                    alert.showAndWait();
                                    Platform.exit();
                                    System.exit(0);
                                }
                            } catch (Exception ex) {
                                Platform.exit();
                                System.exit(0);
                            }
                        });
                    } catch (IllegalStateException e) {
                        System.err.println("JavaFX Toolkit not initialized. Skipping UI logout alert.");
                        stopStatusCheck();
                    }
                }
            }
        } catch (Exception e) {
            // Ignore connection errors
        }
    }

    public boolean isLoggedIn() {
        return userId != null;
    }

    public boolean isSeller() {
        return "SELLER".equalsIgnoreCase(role);
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isBidder() {
        return "BIDDER".equalsIgnoreCase(role);
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullname() { return fullname; }
    public String getRole() { return role; }
}




