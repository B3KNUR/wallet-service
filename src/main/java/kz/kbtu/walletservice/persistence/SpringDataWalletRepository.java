package kz.kbtu.walletservice.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataWalletRepository extends JpaRepository<WalletEntity, Long> {
    List<WalletEntity> findByUserId(Long userId);
}