package kz.kbtu.walletservice.app;

import kz.kbtu.walletservice.app.config.WalletProperties;
import kz.kbtu.walletservice.domain.exception.InsufficientFundsException;
import kz.kbtu.walletservice.domain.exception.WalletAlreadyExistsException;
import kz.kbtu.walletservice.domain.exception.WalletNotFoundException;
import kz.kbtu.walletservice.domain.Wallet;
import kz.kbtu.walletservice.domain.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    private WalletService walletService;

    @BeforeEach
    void setUp() {
        WalletProperties properties = new WalletProperties(
                "KZT",
                new BigDecimal("1000000"),
                List.of("KZT", "USD"),
                new BigDecimal("0"),
                false
        );
        walletService = new WalletService(walletRepository, new ZeroCommissionCalculator(), properties);
    }

    @Test
    void withdraw_decreasesBalanceAndSaves() {
        Wallet wallet = new Wallet(1L, "KZT");
        wallet.deposit(new BigDecimal("5000"));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        walletService.withdraw(1L, new BigDecimal("1000"));

        assertThat(wallet.getBalance()).isEqualByComparingTo("4000");
        verify(walletRepository).save(wallet);
    }

    @Test
    void withdraw_throwsWhenNotEnoughMoneyAndDoesNotSave() {
        Wallet wallet = new Wallet(1L, "KZT");
        wallet.deposit(new BigDecimal("100"));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        assertThatThrownBy(() -> walletService.withdraw(1L, new BigDecimal("500")))
                .isInstanceOf(InsufficientFundsException.class);

        assertThat(wallet.getBalance()).isEqualByComparingTo("100");
        verify(walletRepository, never()).save(any());
    }

    @Test
    void transfer_movesMoneyAndSavesBothWallets() {
        Wallet from = new Wallet(1L, "KZT");
        from.deposit(new BigDecimal("5000"));
        Wallet to = new Wallet(2L, "KZT");
        when(walletRepository.findById(1L)).thenReturn(Optional.of(from));
        when(walletRepository.findById(2L)).thenReturn(Optional.of(to));

        walletService.transfer(1L, 2L, new BigDecimal("1000"));

        assertThat(from.getBalance()).isEqualByComparingTo("4000");
        assertThat(to.getBalance()).isEqualByComparingTo("1000");
        verify(walletRepository).save(from);
        verify(walletRepository).save(to);
    }

    @Test
    void transfer_throwsWhenNotEnoughMoneyAndDoesNotSave() {
        Wallet from = new Wallet(1L, "KZT");
        from.deposit(new BigDecimal("100"));
        Wallet to = new Wallet(2L, "KZT");
        when(walletRepository.findById(1L)).thenReturn(Optional.of(from));
        when(walletRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThatThrownBy(() -> walletService.transfer(1L, 2L, new BigDecimal("1000")))
                .isInstanceOf(InsufficientFundsException.class);

        verify(walletRepository, never()).save(any());
    }

    @Test
    void getById_throwsWhenWalletNotFound() {
        when(walletRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.getById(99L))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_throwsWhenUserAlreadyHasOpenWalletInSameCurrency() {
        Wallet existing = new Wallet(1L, "KZT");
        when(walletRepository.findByUserId(1L)).thenReturn(List.of(existing));

        assertThatThrownBy(() -> walletService.create(1L, "KZT"))
                .isInstanceOf(WalletAlreadyExistsException.class);

        verify(walletRepository, never()).save(any());
    }
}