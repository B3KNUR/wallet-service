package kz.kbtu.walletservice.repository;

import kz.kbtu.walletservice.model.Wallet;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryWalletRepository implements WalletRepository {

    private final Map<Long, Wallet> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public Wallet save(Wallet wallet) {
        if (wallet.getId() == null) {
            wallet.setId(idGenerator.incrementAndGet());
        }
        storage.put(wallet.getId(), wallet);
        return wallet;
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Wallet> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Wallet::getId))
                .toList();
    }

    @Override
    public List<Wallet> findByUserId(Long userId) {
        return storage.values().stream()
                .filter(wallet -> wallet.getUserId().equals(userId))
                .sorted(Comparator.comparing(Wallet::getId))
                .toList();
    }
}