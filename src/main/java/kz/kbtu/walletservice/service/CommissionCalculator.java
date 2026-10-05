package kz.kbtu.walletservice.service;

import java.math.BigDecimal;

public interface CommissionCalculator {
    BigDecimal calculate(BigDecimal amount);
}
