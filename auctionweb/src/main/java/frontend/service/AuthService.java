package frontend.service;

import frontend.utils.BackendClient;
import frontend.utils.SessionManager;
import org.json.JSONObject;

import java.net.http.HttpResponse;

public class AuthService {

    public static class AuthResult {
        public boolean success;
        public String message;

        public AuthResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public AuthResult login(String username, String password) {
        try {
            String jsonBody = String.format(
                    "{\"username\":\"%s\", \"password\":\"%s\"}",
                    escapeJson(username),
                    escapeJson(password)
            );
            HttpResponse<String> response = BackendClient.getInstance().post("/auth/signin", jsonBody);

            if (response.statusCode() == 200) {
                try {
                    JSONObject user = new JSONObject(response.body());
                    SessionManager.getInstance().setUser(
                            user.getLong("id"),
                            user.getString("username"),
                            user.optString("fullname", username),
                            user.optString("role", "BIDDER")
                    );
                } catch (Exception e) {
                    // Fallback
                    SessionManager.getInstance().setUser(0L, username, username, "BIDDER");
                }
                return new AuthResult(true, "Dang nhap thanh cong");
            } else {
                return new AuthResult(false, "Loi: " + response.body());
            }
        } catch (Exception e) {
            return new AuthResult(false, "Khong the ket noi toi may chu!");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
