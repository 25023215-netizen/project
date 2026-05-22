package com.nhom4project.auctionweb.backend.config;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Isolated unit tests for Server Configuration classes.
 * Asserts all pool sizing configurations and prefix routing parameters in pure isolation.
 */
public class ServerConfigTest {

    @Test
    public void testAsyncConfig_TaskExecutorBean() {
        AsyncConfig asyncConfig = new AsyncConfig();
        Executor executor = asyncConfig.taskExecutor();

        assertTrue(executor instanceof ThreadPoolTaskExecutor);
        ThreadPoolTaskExecutor poolExecutor = (ThreadPoolTaskExecutor) executor;

        assertEquals(8, poolExecutor.getCorePoolSize());
        assertEquals(32, poolExecutor.getMaxPoolSize());
        assertEquals("auction-async-", poolExecutor.getThreadNamePrefix());
        poolExecutor.shutdown();
    }

    @Test
    public void testSchedulerConfig_ConfiguresTasks() {
        SchedulerConfig schedulerConfig = new SchedulerConfig();
        ScheduledTaskRegistrar registrar = mock(ScheduledTaskRegistrar.class);

        schedulerConfig.configureTasks(registrar);

        ArgumentCaptor<TaskScheduler> schedulerCaptor = ArgumentCaptor.forClass(TaskScheduler.class);
        verify(registrar).setTaskScheduler(schedulerCaptor.capture());

        TaskScheduler scheduler = schedulerCaptor.getValue();
        assertTrue(scheduler instanceof ThreadPoolTaskScheduler);

        ThreadPoolTaskScheduler taskScheduler = (ThreadPoolTaskScheduler) scheduler;
        assertEquals(4, taskScheduler.getScheduledThreadPoolExecutor().getCorePoolSize());
        assertEquals("auction-sched-", taskScheduler.getThreadNamePrefix());
        taskScheduler.shutdown();
    }

    @Test
    public void testWebSocketConfig_MessageBroker() {
        WebSocketConfig webSocketConfig = new WebSocketConfig();
        MessageBrokerRegistry brokerRegistry = mock(MessageBrokerRegistry.class);

        webSocketConfig.configureMessageBroker(brokerRegistry);

        verify(brokerRegistry).enableSimpleBroker("/topic");
        verify(brokerRegistry).setApplicationDestinationPrefixes("/app");
    }

    @Test
    public void testWebSocketConfig_StompEndpoints() {
        WebSocketConfig webSocketConfig = new WebSocketConfig();
        StompEndpointRegistry endpointRegistry = mock(StompEndpointRegistry.class);
        StompWebSocketEndpointRegistration registration = mock(StompWebSocketEndpointRegistration.class);

        when(endpointRegistry.addEndpoint("/ws")).thenReturn(registration);
        when(registration.setAllowedOriginPatterns("*")).thenReturn(registration);

        webSocketConfig.registerStompEndpoints(endpointRegistry);

        verify(endpointRegistry).addEndpoint("/ws");
        verify(registration).setAllowedOriginPatterns("*");
        verify(registration).withSockJS();
    }
}
