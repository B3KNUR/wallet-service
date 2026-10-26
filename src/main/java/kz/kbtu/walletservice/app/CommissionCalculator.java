package kz.kbtu.walletservice.app;

import java.math.BigDecimal;

public interface CommissionCalculator {
    BigDecimal calculate(BigDecimal amount);
}
