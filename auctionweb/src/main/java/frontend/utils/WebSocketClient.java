package frontend.utils;

import javafx.application.Platform;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * WebSocket client cho JavaFX frontend.
 * Sử dụng STOMP-like protocol qua plain WebSocket.
 * Cho phép subscribe vào topic và nhận callback khi có message mới.
 *
 * Đây là phần client-side của Observer Pattern:
 * - Server broadcast qua SimpMessagingTemplate
 * - Client nhận thông qua polling HTTP (fallback) hoặc WebSocket
 *
 * Lưu ý: Spring STOMP qua SockJS yêu cầu thư viện STOMP client phức tạp.
 * Giải pháp đơn giản hơn: dùng polling HTTP kết hợp với callbacks.
 */
public class WebSocketClient {
    private static WebSocketClient instance;
    private final Map<String, CopyOnWriteArrayList<Consumer<String>>> listeners = new ConcurrentHashMap<>();
    private volatile boolean running = false;
    private Thread pollingThread;

    private WebSocketClient() {}

    public static synchronized WebSocketClient getInstance() {
        if (instance == null) {
            instance = new WebSocketClient();
        }
        return instance;
    }

    /**
     * Đăng ký listener cho một auction cụ thể.
     */
    public void subscribe(String topic, Consumer<String> listener) {
        listeners.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    /**
     * Hủy listener.
     */
    public void unsubscribe(String topic, Consumer<String> listener) {
        CopyOnWriteArrayList<Consumer<String>> list = listeners.get(topic);
        if (list != null) {
            list.remove(listener);
        }
    }

    /**
     * Bắt đầu polling realtime cho một auction cụ thể.
     * Polling mỗi 2 giây - cách tiếp cận đơn giản nhưng hiệu quả cho JavaFX client.
     */
    public void startPolling(Long auctionId) {
        if (running) return;
        running = true;

        pollingThread = new Thread(() -> {
            String lastData = "";
            while (running) {
                try {
                    var response = BackendClient.getInstance().get("/auctions/" + auctionId);
                    if (response.statusCode() == 200) {
                        String data = response.body();
                        if (!data.equals(lastData)) {
                            lastData = data;
                            String topic = "/topic/auctions/" + auctionId;
                            CopyOnWriteArrayList<Consumer<String>> topicListeners = listeners.get(topic);
                            if (topicListeners != null) {
                                for (Consumer<String> listener : topicListeners) {
                                    Platform.runLater(() -> listener.accept(data));
                                }
                            }
                        }
                    }
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    // Ignore connection errors
                    try { Thread.sleep(3000); } catch (InterruptedException ignored) { break; }
                }
            }
        });
        pollingThread.setDaemon(true);
        pollingThread.start();
    }

    /**
     * Dừng polling.
     */
    public void stopPolling() {
        running = false;
        if (pollingThread != null) {
            pollingThread.interrupt();
        }
        listeners.clear();
    }
}
