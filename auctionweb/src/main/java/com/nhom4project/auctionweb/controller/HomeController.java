package com.nhom4project.auctionweb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String hello() {
        return "<h1>Backend Auction Web đang chạy thành công!</h1><p>Database đã kết nối, Security đã tạm tắt.</p>";
    }
}
