package kz.kbtu.walletservice.service;

import kz.kbtu.walletservice.WalletProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "wallet", name = "commission-enabled", havingValue = "true")
public class StandardCommissionCalculator implements CommissionCalculator {

    private final WalletProperties walletProperties;

    public StandardCommissionCalculator(WalletProperties walletProperties) {
        this.walletProperties = walletProperties;
    }

    @Override
    public double calculate(double amount) {
        return amount * walletProperties.commissionFee();
    }
}
