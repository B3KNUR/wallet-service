package kz.kbtu.walletservice.domain;

import kz.kbtu.walletservice.domain.Wallet;

import java.math.BigDecimal;

public record TransferResult(
        Wallet from,
        Wallet to,
        BigDecimal amount,
        BigDecimal fee
) {}