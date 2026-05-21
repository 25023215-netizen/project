package com.nhom4project.auctionweb.client.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Tiện ích ghi nhận lỗi (Error Logger) của ứng dụng client.
 * Lưu thông tin lỗi và stack trace vào file log_error.txt ở thư mục gốc của ứng dụng.
 */
public class ErrorLogger {
    private static final String LOG_FILE_PATH = "log_error.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static synchronized void log(String message) {
        log(message, null);
    }

    public static synchronized void log(Throwable throwable) {
        if (throwable != null) {
            log(throwable.getMessage(), throwable);
        }
    }

    public static synchronized void log(String message, Throwable throwable) {
        try {
            File file = new File(LOG_FILE_PATH);
            try (FileWriter fw = new FileWriter(file, true);
                 PrintWriter pw = new PrintWriter(fw)) {
                
                String timestamp = LocalDateTime.now().format(formatter);
                pw.println("[" + timestamp + "] ERROR: " + (message != null ? message : "No message"));
                if (throwable != null) {
                    throwable.printStackTrace(pw);
                }
                pw.println("--------------------------------------------------------------------------------");
            }
        } catch (Exception e) {
            System.err.println("Không thể ghi log lỗi vào file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
