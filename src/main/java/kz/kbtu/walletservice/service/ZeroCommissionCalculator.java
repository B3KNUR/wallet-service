package kz.kbtu.walletservice.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "wallet", name = "commission-enabled", havingValue = "false", matchIfMissing = true)
public class ZeroCommissionCalculator implements CommissionCalculator{

    @Override
    public double calculate(double amount) {
        return 0.0;
    }
}
