package kz.kbtu.walletservice.persistence;

import kz.kbtu.walletservice.domain.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletPersistenceMapper {

    public WalletEntity toEntity(Wallet wallet) {
        return new WalletEntity(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getCurrency(),
                wallet.getBalance(),
                wallet.getStatus(),
                wallet.getCreatedAt()
        );
    }

    public Wallet toDomain(WalletEntity entity) {
        return new Wallet(
                entity.getId(),
                entity.getUserId(),
                entity.getCurrency(),
                entity.getBalance(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}