package kz.kbtu.walletservice.app;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConditionalOnProperty(prefix = "wallet", name = "commission-enabled", havingValue = "false", matchIfMissing = true)
public class ZeroCommissionCalculator implements CommissionCalculator{

    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return BigDecimal.ZERO;
    }
}
