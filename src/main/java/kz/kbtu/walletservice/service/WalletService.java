package kz.kbtu.walletservice.service;

import kz.kbtu.walletservice.config.WalletProperties;
import kz.kbtu.walletservice.enums.WalletStatus;
import kz.kbtu.walletservice.exception.BusinessRuleException;
import kz.kbtu.walletservice.exception.InsufficientFundsException;
import kz.kbtu.walletservice.exception.WalletAlreadyExistsException;
import kz.kbtu.walletservice.exception.WalletNotFoundException;
import kz.kbtu.walletservice.domain.TransferResult;
import kz.kbtu.walletservice.domain.Wallet;
import kz.kbtu.walletservice.domain.WalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

    @Transactional
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

    @Transactional(readOnly = true)
    public Wallet getById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<Wallet> getAll(Pageable pageable) {
        return walletRepository.findAll(pageable);
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

    @Transactional
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

    @Transactional
    public Wallet deposit(Long id, BigDecimal amount) {
        requirePositive(amount);
        Wallet wallet = getActiveWallet(id);

        wallet.deposit(amount);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet withdraw(Long id, BigDecimal amount) {
        requirePositive(amount);
        Wallet wallet = getActiveWallet(id);

        wallet.withdraw(amount);
        return walletRepository.save(wallet);
    }
    @Transactional
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

        from.withdraw(totalDebit);
        to.deposit(amount);

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
        wallet.ensureActive();
        return wallet;
    }

    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Amount must be positive");
        }
    }
}
