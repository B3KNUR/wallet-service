package kz.kbtu.walletservice.model;

import java.math.BigDecimal;

public record TransferResult(
        Wallet from,
        Wallet to,
        BigDecimal amount,
        BigDecimal fee
) {}