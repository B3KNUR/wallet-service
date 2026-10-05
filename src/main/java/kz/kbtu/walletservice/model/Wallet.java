package kz.kbtu.walletservice.model;

import kz.kbtu.walletservice.enums.WalletStatus;

import java.math.BigDecimal;
import java.time.Instant;

public class Wallet {

    private Long id;
    private Long userId;
    private String currency;
    private BigDecimal balance;
    private WalletStatus status;
    private Instant createdAt;

    public Wallet() {}

    public Wallet(Long userId, String currency) {
        this.userId = userId;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
        this.status = WalletStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public WalletStatus getStatus() {
        return status;
    }

    public void setStatus(WalletStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
