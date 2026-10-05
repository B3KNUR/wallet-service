package kz.kbtu.walletservice.repository;

import kz.kbtu.walletservice.model.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletRepository {
    Wallet save(Wallet wallet);
    Optional<Wallet> findById(Long id);
    List<Wallet> findAll();
    List<Wallet> findByUserId(Long id);
}