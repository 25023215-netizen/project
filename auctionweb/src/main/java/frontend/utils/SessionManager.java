package frontend.utils;

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
    }

    public void clear() {
        this.userId = null;
        this.username = null;
        this.fullname = null;
        this.role = null;
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
