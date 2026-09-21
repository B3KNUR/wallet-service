package kz.kbtu.walletservice.controller;

import kz.kbtu.walletservice.service.WalletService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/fee")
    public Map<String, Object> getFee(@RequestParam(defaultValue = "1000") double amount) {
        double fee = walletService.calculateFee(amount);
        return Map.of(
                "amount", amount,
                "calculatedFee", fee
        );
    }
}
