package kz.kbtu.walletservice.web.dto.response;

import kz.kbtu.walletservice.domain.WalletStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record WalletResponse(
        Long id,
        Long userId,
        String currency,
        BigDecimal balance,
        WalletStatus status,
        Instant createdAt) {}
