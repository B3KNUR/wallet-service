package kz.kbtu.walletservice.domain;

import kz.kbtu.walletservice.domain.exception.BusinessRuleException;
import kz.kbtu.walletservice.domain.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.time.Instant;

public class Wallet {

    private final Long id;
    private final Long userId;
    private final String currency;
    private BigDecimal balance;
    private WalletStatus status;
    private final Instant createdAt;

    public Wallet(Long userId, String currency) {
        this(null, userId, currency, BigDecimal.ZERO, WalletStatus.ACTIVE, Instant.now());
    }

    public Wallet(Long id, Long userId, String currency, BigDecimal balance,
                  WalletStatus status, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(id, balance, amount);
        }
        balance = balance.subtract(amount);
    }

    public void ensureActive() {
        if (status != WalletStatus.ACTIVE) {
            throw new BusinessRuleException("Wallet " + id + " is " + status);
        }
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getCurrency() { return currency; }
    public BigDecimal getBalance() { return balance; }
    public WalletStatus getStatus() { return status; }
    public void setStatus(WalletStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
}