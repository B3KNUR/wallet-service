package kz.kbtu.walletservice.service;

import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private final CommissionCalculator commissionCalculator;

    public WalletService(CommissionCalculator commissionCalculator) {
        this.commissionCalculator = commissionCalculator;
    }

    public double calculateFee(double amount) {
        return commissionCalculator.calculate(amount);
    }
}
