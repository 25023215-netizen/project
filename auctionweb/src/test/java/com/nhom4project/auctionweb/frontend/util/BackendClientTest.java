package com.nhom4project.auctionweb.frontend.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BackendClient.
 * Resets the BackendClient singleton prior to run to prevent test pollution,
 * then injects mock HttpClient for isolation.
 */
public class BackendClientTest {

    private BackendClient backendClient;
    private HttpClient mockHttpClient;
    private HttpResponse<String> mockResponse;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        // Reset BackendClient singleton instance to prevent test pollution
        Field instanceField = BackendClient.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        backendClient = BackendClient.getInstance();
        mockHttpClient = mock(HttpClient.class);
        mockResponse = mock(HttpResponse.class);

        // Inject mock HttpClient
        Field httpClientField = BackendClient.class.getDeclaredField("httpClient");
        httpClientField.setAccessible(true);
        httpClientField.set(backendClient, mockHttpClient);

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"success\":true}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
    }

    @Test
    public void testGetRequest() throws Exception {
        HttpResponse<String> response = backendClient.get("/test-endpoint");

        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals("{\"success\":true}", response.body());

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any());

        HttpRequest capturedRequest = requestCaptor.getValue();
        assertEquals("GET", capturedRequest.method());
        assertTrue(capturedRequest.uri().toString().endsWith("/api/test-endpoint"));
    }

    @Test
    public void testPostRequest() throws Exception {
        String jsonBody = "{\"name\":\"item\"}";
        HttpResponse<String> response = backendClient.post("/test-endpoint", jsonBody);

        assertNotNull(response);
        assertEquals(200, response.statusCode());

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any());

        HttpRequest capturedRequest = requestCaptor.getValue();
        assertEquals("POST", capturedRequest.method());
        assertTrue(capturedRequest.uri().toString().endsWith("/api/test-endpoint"));
    }

    @Test
    public void testPutRequest() throws Exception {
        String jsonBody = "{\"status\":\"ACTIVE\"}";
        HttpResponse<String> response = backendClient.put("/test-endpoint", jsonBody);

        assertNotNull(response);
        assertEquals(200, response.statusCode());

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any());

        HttpRequest capturedRequest = requestCaptor.getValue();
        assertEquals("PUT", capturedRequest.method());
        assertTrue(capturedRequest.uri().toString().endsWith("/api/test-endpoint"));
    }

    @Test
    public void testDeleteRequest() throws Exception {
        HttpResponse<String> response = backendClient.delete("/test-endpoint");

        assertNotNull(response);
        assertEquals(200, response.statusCode());

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any());

        HttpRequest capturedRequest = requestCaptor.getValue();
        assertEquals("DELETE", capturedRequest.method());
        assertTrue(capturedRequest.uri().toString().endsWith("/api/test-endpoint"));
    }
}
