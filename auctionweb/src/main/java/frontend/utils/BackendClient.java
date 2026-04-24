package frontend.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * BackendClient triển khai theo mẫu thiết kế Singleton.
 * Dùng để gửi các yêu cầu HTTP tới Spring Boot Backend.
 */
public class BackendClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static BackendClient instance;
    private final HttpClient httpClient;

    private BackendClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
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
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> get(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
