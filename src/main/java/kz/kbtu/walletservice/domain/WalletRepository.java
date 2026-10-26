package kz.kbtu.walletservice.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WalletRepository {
    Wallet save(Wallet wallet);
    Optional<Wallet> findById(Long id);
    Page<Wallet> findAll(Pageable pageable);
    List<Wallet> findByUserId(Long userId);
}