package kz.kbtu.walletservice.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kz.kbtu.walletservice.web.dto.request.AmountRequest;
import kz.kbtu.walletservice.web.dto.request.CreateWalletRequest;
import kz.kbtu.walletservice.web.dto.request.UpdateStatusRequest;
import kz.kbtu.walletservice.web.dto.response.PageResponse;
import kz.kbtu.walletservice.web.dto.response.WalletResponse;
import kz.kbtu.walletservice.web.mapper.WalletMapper;
import kz.kbtu.walletservice.domain.Wallet;
import kz.kbtu.walletservice.app.WalletService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {
    private final WalletService walletService;
    private final WalletMapper walletMapper;

    public WalletController(WalletService walletService, WalletMapper walletMapper) {
        this.walletService = walletService;
        this.walletMapper = walletMapper;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> create(@Valid @RequestBody CreateWalletRequest request,
                                                 UriComponentsBuilder uriBuilder) {
        Wallet wallet = walletService.create(request.userId(), request.currency());

        URI location = uriBuilder
                .path("/api/v1/wallets/{id}")
                .buildAndExpand(wallet.getId())
                .toUri();

        return ResponseEntity.created(location).body(walletMapper.toWalletResponse(wallet));
    }

    @GetMapping
    public PageResponse<WalletResponse> getAll(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return walletMapper.toWalletPageResponse(walletService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public WalletResponse getById(@PathVariable @Positive Long id) {
        return walletMapper.toWalletResponse(walletService.getById(id));
    }

    @PatchMapping("/{id}/status")
    public WalletResponse updateStatus(@PathVariable @Positive Long id,
                                       @Valid @RequestBody UpdateStatusRequest request) {
        Wallet wallet = walletService.updateStatus(id, request.status());
        return walletMapper.toWalletResponse(wallet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> close(@PathVariable @Positive Long id) {
        walletService.close(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deposits")
    public WalletResponse deposit(@PathVariable @Positive Long id,
                                  @Valid @RequestBody AmountRequest request) {
        Wallet wallet = walletService.deposit(id, request.amount());
        return walletMapper.toWalletResponse(wallet);
    }

    @PostMapping("/{id}/withdrawals")
    public WalletResponse withdraw(@PathVariable @Positive Long id,
                                   @Valid @RequestBody AmountRequest request) {
        Wallet wallet = walletService.withdraw(id, request.amount());
        return walletMapper.toWalletResponse(wallet);
    }
}

