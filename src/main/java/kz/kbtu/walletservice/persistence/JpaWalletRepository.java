package kz.kbtu.walletservice.persistence;

import kz.kbtu.walletservice.domain.Wallet;
import kz.kbtu.walletservice.domain.WalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaWalletRepository implements WalletRepository {

    private final SpringDataWalletRepository springData;
    private final WalletPersistenceMapper mapper;

    public JpaWalletRepository(SpringDataWalletRepository springData, WalletPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public Wallet save(Wallet wallet) {
        WalletEntity saved = springData.save(mapper.toEntity(wallet));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Wallet> findAll(Pageable pageable) {
        return springData.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public List<Wallet> findByUserId(Long userId) {
        List<Wallet> result = new ArrayList<>();
        for (WalletEntity entity : springData.findByUserId(userId)) {
            result.add(mapper.toDomain(entity));
        }
        return result;
    }
}