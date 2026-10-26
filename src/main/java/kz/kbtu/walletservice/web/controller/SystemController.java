package kz.kbtu.walletservice.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class SystemController {

    @GetMapping("/api/v1/status")
    public Map<String, Object> getStatus() {
        return Map.of(
                "service", "wallet-service",
                "status", "UP",
                "timestamp", LocalDateTime.now()
        );
    }
}