package com.nhom4project.auctionweb.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Cấu hình Thread Pool để xử lý các tác vụ bất đồng bộ (@Async).
 * Giúp giải phóng HTTP Request thread ngay lập tức khi chạy các tiến trình ngầm (như Auto-bid, WebSocket Broadcast).
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);          // Số lượng luồng tối thiểu luôn được duy trì
        executor.setMaxPoolSize(32);          // Số lượng luồng tối đa khi hàng đợi bị đầy
        executor.setQueueCapacity(1000);      // Sức chứa của hàng đợi trước khi tạo thêm luồng mới
        executor.setThreadNamePrefix("auction-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true); // Đảm bảo hoàn thành các tác vụ đang chạy khi tắt ứng dụng
        executor.setAwaitTerminationSeconds(60);            // Thời gian chờ tối đa khi tắt ứng dụng
        executor.initialize();
        return executor;
    }
}
