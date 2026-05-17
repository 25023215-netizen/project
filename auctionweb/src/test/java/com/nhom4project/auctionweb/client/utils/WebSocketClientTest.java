package com.nhom4project.auctionweb.client.utils;

import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WebSocketClient.
 * Boots the JavaFX runtime in @BeforeAll to safely execute Platform.runLater across threads,
 * and resets singletons to guarantee complete test isolation.
 */
public class WebSocketClientTest {

    private WebSocketClient webSocketClient;
    private HttpClient mockHttpClient;

    @BeforeAll
    public static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Already initialized
        }
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        // Reset BackendClient singleton instance to prevent test pollution
        Field backendInstanceField = BackendClient.class.getDeclaredField("instance");
        backendInstanceField.setAccessible(true);
        backendInstanceField.set(null, null);

        // Reset WebSocketClient singleton instance to prevent test pollution
        Field wsInstanceField = WebSocketClient.class.getDeclaredField("instance");
        wsInstanceField.setAccessible(true);
        wsInstanceField.set(null, null);

        webSocketClient = WebSocketClient.getInstance();
        webSocketClient.stopPolling();

        // Inject the mock HttpClient for BackendClient to intercept REST GET calls
        mockHttpClient = mock(HttpClient.class);
        Field field = BackendClient.class.getDeclaredField("httpClient");
        field.setAccessible(true);
        field.set(BackendClient.getInstance(), mockHttpClient);
    }

    @AfterEach
    public void tearDown() {
        webSocketClient.stopPolling();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSubscribeAndUnsubscribe() {
        String topic = "/topic/auctions/1";
        List<String> results = new CopyOnWriteArrayList<>();
        Consumer<String> listener = results::add;

        webSocketClient.subscribe(topic, listener);

        assertDoesNotThrow(() -> {
            Field listenersField = WebSocketClient.class.getDeclaredField("listeners");
            listenersField.setAccessible(true);
            java.util.Map<String, CopyOnWriteArrayList<Consumer<String>>> map =
                    (java.util.Map<String, CopyOnWriteArrayList<Consumer<String>>>) listenersField.get(webSocketClient);
            
            assertTrue(map.containsKey(topic));
            assertEquals(1, map.get(topic).size());
        });

        webSocketClient.unsubscribe(topic, listener);

        assertDoesNotThrow(() -> {
            Field listenersField = WebSocketClient.class.getDeclaredField("listeners");
            listenersField.setAccessible(true);
            java.util.Map<String, CopyOnWriteArrayList<Consumer<String>>> map =
                    (java.util.Map<String, CopyOnWriteArrayList<Consumer<String>>>) listenersField.get(webSocketClient);
            
            assertEquals(0, map.get(topic).size());
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPollingLifecycleAndNotifications() throws Exception {
        HttpResponse mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"price\":150}");
        
        // Use generic matchers any() to prevent generic type matching constraints
        when(mockHttpClient.send(any(HttpRequest.class), any()))
                .thenReturn(mockResponse);

        String topic = "/topic/auctions/5";
        CountDownLatch latch = new CountDownLatch(1);
        List<String> receivedData = new CopyOnWriteArrayList<>();

        webSocketClient.subscribe(topic, data -> {
            receivedData.add(data);
            latch.countDown();
        });

        // Start background polling
        webSocketClient.startPolling(5L);

        // Wait for the background thread to fetch data and trigger the listener via the JavaFX thread
        boolean success = latch.await(6, TimeUnit.SECONDS);

        assertTrue(success, "The listener was not notified within the timeout period.");
        assertEquals(1, receivedData.size());
        assertEquals("{\"price\":150}", receivedData.get(0));

        webSocketClient.stopPolling();
    }
}
