package kz.kbtu.walletservice.controller;

import jakarta.validation.Valid;
import kz.kbtu.walletservice.dto.request.TransferRequest;
import kz.kbtu.walletservice.dto.response.TransferResponse;
import kz.kbtu.walletservice.mapper.WalletMapper;
import kz.kbtu.walletservice.model.TransferResult;
import kz.kbtu.walletservice.service.WalletService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {
    private final WalletService walletService;
    private final WalletMapper walletMapper;

    public TransferController(WalletService walletService, WalletMapper walletMapper) {
        this.walletService = walletService;
        this.walletMapper = walletMapper;
    }

    @PostMapping
    public TransferResponse transfer(@Valid @RequestBody TransferRequest request) {
        TransferResult result = walletService.transfer(
                request.fromWalletId(),
                request.toWalletId(),
                request.amount()
        );
        return walletMapper.toTransferResponse(result);
    }
}