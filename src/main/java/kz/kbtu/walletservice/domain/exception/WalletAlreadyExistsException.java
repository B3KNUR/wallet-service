package kz.kbtu.walletservice.domain.exception;

public class WalletAlreadyExistsException extends RuntimeException {

    private final Long existingWalletId;

    public WalletAlreadyExistsException(Long userId, String currency, Long existingWalletId) {
        super("User " + userId + " already has an open " + currency + " wallet");
        this.existingWalletId = existingWalletId;
    }

    public Long getExistingWalletId() {
        return existingWalletId;
    }
}
