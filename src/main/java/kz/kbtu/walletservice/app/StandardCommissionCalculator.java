package kz.kbtu.walletservice.app;

import kz.kbtu.walletservice.app.config.WalletProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@ConditionalOnProperty(prefix = "wallet", name = "commission-enabled", havingValue = "true")
public class StandardCommissionCalculator implements CommissionCalculator {

    private final WalletProperties walletProperties;

    public StandardCommissionCalculator(WalletProperties walletProperties) {
        this.walletProperties = walletProperties;
    }

    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(walletProperties.commissionFee()).setScale(2, RoundingMode.HALF_EVEN);
    }
}
