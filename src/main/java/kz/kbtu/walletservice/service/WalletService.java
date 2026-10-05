package kz.kbtu.walletservice.service;

import kz.kbtu.walletservice.config.WalletProperties;
import kz.kbtu.walletservice.enums.WalletStatus;
import kz.kbtu.walletservice.exception.BusinessRuleException;
import kz.kbtu.walletservice.exception.InsufficientFundsException;
import kz.kbtu.walletservice.exception.WalletAlreadyExistsException;
import kz.kbtu.walletservice.exception.WalletNotFoundException;
import kz.kbtu.walletservice.model.TransferResult;
import kz.kbtu.walletservice.model.Wallet;
import kz.kbtu.walletservice.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final CommissionCalculator commissionCalculator;
    private final WalletProperties walletProperties;

    public WalletService(WalletRepository walletRepository,
                         CommissionCalculator commissionCalculator,
                         WalletProperties walletProperties) {
        this.walletRepository = walletRepository;
        this.commissionCalculator = commissionCalculator;
        this.walletProperties = walletProperties;
    }


    public Wallet create(Long userId, String currency) {
        String normalizedCurrency = currency.toUpperCase();

        if (!walletProperties.allowedCurrencies().contains(normalizedCurrency)) {
            throw new BusinessRuleException(
                    "Currency " + currency + " is not allowed. Allowed: " + walletProperties.allowedCurrencies()
            );
        }

        walletRepository.findByUserId(userId).stream()
                .filter(w -> w.getCurrency().equals(normalizedCurrency))
                .filter(w -> w.getStatus() != WalletStatus.CLOSED)
                .findFirst()
                .ifPresent(existing -> {
                    throw new WalletAlreadyExistsException(userId, normalizedCurrency, existing.getId());
                });

        Wallet wallet = new Wallet(userId, normalizedCurrency);
        return walletRepository.save(wallet);
    }

    public Wallet getById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException(id));
    }

    public List<Wallet> getAll() {
        return walletRepository.findAll();
    }

    public Wallet updateStatus(Long id, WalletStatus newStatus) {
        Wallet wallet = getById(id);

        if (wallet.getStatus() == WalletStatus.CLOSED) {
            throw new BusinessRuleException("Wallet " + id + " is closed and cannot be changed");
        }
        if (newStatus == WalletStatus.CLOSED) {
            throw new BusinessRuleException("Use DELETE to close a wallet");
        }

        wallet.setStatus(newStatus);
        return walletRepository.save(wallet);
    }

    public void close(Long id) {
        Wallet wallet = getById(id);

        if (wallet.getStatus() == WalletStatus.CLOSED) {
            return;
        }
        if (wallet.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessRuleException(
                    "Wallet " + id + " has balance " + wallet.getBalance() + ". Withdraw all funds before closing"
            );
        }

        wallet.setStatus(WalletStatus.CLOSED);
        walletRepository.save(wallet);
    }


    public Wallet deposit(Long id, BigDecimal amount) {
        requirePositive(amount);
        Wallet wallet = getActiveWallet(id);

        wallet.setBalance(wallet.getBalance().add(amount));
        return walletRepository.save(wallet);
    }

    public Wallet withdraw(Long id, BigDecimal amount) {
        requirePositive(amount);
        Wallet wallet = getActiveWallet(id);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(id, wallet.getBalance(), amount);
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        return walletRepository.save(wallet);
    }

    public TransferResult transfer(Long fromId, Long toId, BigDecimal amount) {
        requirePositive(amount);
        if (fromId.equals(toId)) {
            throw new BusinessRuleException("Cannot transfer to the same wallet");
        }
        if (amount.compareTo(walletProperties.maxTransferLimit()) > 0) {
            throw new BusinessRuleException(
                    "Transfer amount " + amount + " exceeds limit " + walletProperties.maxTransferLimit()
            );
        }

        Wallet from = getActiveWallet(fromId);
        Wallet to = getActiveWallet(toId);

        if (!from.getCurrency().equals(to.getCurrency())) {
            throw new BusinessRuleException(
                    "Currency mismatch: " + from.getCurrency() + " -> " + to.getCurrency()
            );
        }

        BigDecimal fee = commissionCalculator.calculate(amount);
        BigDecimal totalDebit = amount.add(fee);

        if (from.getBalance().compareTo(totalDebit) < 0) {
            throw new InsufficientFundsException(fromId, from.getBalance(), totalDebit);
        }

        from.setBalance(from.getBalance().subtract(totalDebit));
        to.setBalance(to.getBalance().add(amount));

        walletRepository.save(from);
        walletRepository.save(to);

        return new TransferResult(from, to, amount, fee);
    }

    public BigDecimal calculateFee(BigDecimal amount) {
        requirePositive(amount);
        return commissionCalculator.calculate(amount);
    }


    private Wallet getActiveWallet(Long id) {
        Wallet wallet = getById(id);
        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new BusinessRuleException("Wallet " + id + " is " + wallet.getStatus());
        }
        return wallet;
    }

    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Amount must be positive");
        }
    }
}
