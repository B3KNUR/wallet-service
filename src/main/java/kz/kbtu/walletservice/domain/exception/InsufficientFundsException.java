package kz.kbtu.walletservice.domain.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    private final Long walletId;
    private final BigDecimal balance;
    private final BigDecimal required;

    public InsufficientFundsException(Long walletId, BigDecimal balance, BigDecimal required) {
        super("Wallet " + walletId + " has " + balance + ", but " + required + " is required");
        this.walletId = walletId;
        this.balance = balance;
        this.required = required;
    }

    public Long getWalletId() { return walletId; }
    public BigDecimal getBalance() { return balance; }
    public BigDecimal getRequired() { return required; }
}