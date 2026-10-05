package kz.kbtu.walletservice.dto.response;

import java.math.BigDecimal;

public record TransferResponse(
        Long fromWalletId,
        Long toWalletId,
        String currency,
        BigDecimal amount,
        BigDecimal fee,
        BigDecimal totalDebited,
        BigDecimal fromBalanceAfter
) {}
