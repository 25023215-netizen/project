package com.nhom4project.auctionweb.controller.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String hello() {
        return "<h1>Auction Web Backend is running successfully!</h1><p>Database connected. Security is temporarily disabled.</p>";
    }
}




