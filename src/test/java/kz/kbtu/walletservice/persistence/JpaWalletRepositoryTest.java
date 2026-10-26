package kz.kbtu.walletservice.persistence;

import kz.kbtu.walletservice.domain.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Testcontainers
@Import({JpaWalletRepository.class, WalletPersistenceMapper.class})
@TestPropertySource(properties = "spring.test.database.replace=none")
class JpaWalletRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private JpaWalletRepository repository;

    @Test
    void save_assignsIdAndWalletCanBeFoundById() {
        Wallet saved = repository.save(new Wallet(500L, "KZT"));

        assertThat(saved.getId()).isNotNull();

        Optional<Wallet> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCurrency()).isEqualTo("KZT");
        assertThat(found.get().getBalance()).isEqualByComparingTo("0");
    }

    @Test
    void findByUserId_returnsWalletsFromSeedMigration() {
        List<Wallet> wallets = repository.findByUserId(100L);

        assertThat(wallets).hasSize(2);
    }

    @Test
    void findAll_returnsRequestedPage() {
        Page<Wallet> page = repository.findAll(PageRequest.of(0, 2, Sort.by("id")));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(4);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    void save_rejectsSecondOpenWalletInSameCurrencyForSameUser() {
        repository.save(new Wallet(500L, "KZT"));

        assertThatThrownBy(() -> repository.save(new Wallet(500L, "KZT")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}