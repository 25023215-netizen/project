package com.nhom4project.auctionweb.client.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Singleton HTTP client cho giao tiếp với Backend REST API.
 * Design pattern: Singleton
 */
public class BackendClient {
    private static String BASE_URL = "http://localhost:8080/api";
    private static BackendClient instance;
    private final HttpClient httpClient;

    static {
        try {
            java.util.Properties props = new java.util.Properties();
            java.io.File configFile = new java.io.File("config.properties");
            if (configFile.exists()) {
                try (java.io.FileInputStream fis = new java.io.FileInputStream(configFile)) {
                    props.load(fis);
                    String host = props.getProperty("server.host", "localhost").trim();
                    String port = props.getProperty("server.port", "8080").trim();
                    
                    if (host.startsWith("http")) {
                        // If it's a full URL (like ngrok), use it directly
                        BASE_URL = host;
                    } else {
                        // Otherwise construct it
                        BASE_URL = "http://" + host + ":" + port;
                    }
                    
                    // Ensure it doesn't end with a slash before adding /api
                    if (BASE_URL.endsWith("/")) {
                        BASE_URL = BASE_URL.substring(0, BASE_URL.length() - 1);
                    }
                    
                    if (!BASE_URL.endsWith("/api")) {
                        BASE_URL += "/api";
                    }
                    
                    System.out.println(">>> BackendClient initialized with: " + BASE_URL);
                }
            } else {
                // Create default config file if it doesn't exist
                props.setProperty("server.host", "localhost");
                props.setProperty("server.port", "8080");
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(configFile)) {
                    props.store(fos, "AuctionWeb Client Configuration");
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load config.properties, using default: " + e.getMessage());
        }
    }

    private BackendClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(60))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

    public static synchronized BackendClient getInstance() {
        if (instance == null) {
            instance = new BackendClient();
        }
        return instance;
    }

    public HttpResponse<String> post(String endpoint, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("ngrok-skip-browser-warning", "true")
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
=======
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
=======
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> get(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .header("ngrok-skip-browser-warning", "true")
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
=======
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
=======
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> put(String endpoint, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("ngrok-skip-browser-warning", "true")
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
=======
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
=======
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> delete(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .header("ngrok-skip-browser-warning", "true")
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
=======
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
=======
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
                .DELETE()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
=======
=======
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java

    /**
     * Helper method to extract a clean, user-friendly error message from non-200 HTTP responses.
     * Prevents displaying raw HTML code when ngrok returns warning or bad gateway pages.
     */
    public static String getCleanErrorMessage(HttpResponse<String> response) {
        if (response == null) {
            return "Không nhận được phản hồi từ máy chủ!";
        }
        String body = response.body();
        if (body == null || body.trim().isEmpty()) {
            return "Lỗi phản hồi rỗng từ máy chủ (Status: " + response.statusCode() + ")";
        }
        
        String trimmedBody = body.trim();
        if (trimmedBody.contains("<!DOCTYPE") || trimmedBody.contains("<html") || trimmedBody.contains("ngrok")) {
            return "Lỗi kết nối máy chủ qua Ngrok (Giới hạn Traffic/Server offline). Vui lòng thử lại! (Mã lỗi: " + response.statusCode() + ")";
        }
        return "Lỗi: " + trimmedBody;
    }
<<<<<<< HEAD:PJB/auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
>>>>>>> main:auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
=======
>>>>>>> f722d627f510dd91cb2323c2d79d99f63b52b9b8:auctionweb/src/main/java/com/nhom4project/auctionweb/client/utils/BackendClient.java
}




