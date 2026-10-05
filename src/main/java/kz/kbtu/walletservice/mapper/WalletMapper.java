package kz.kbtu.walletservice.mapper;

import kz.kbtu.walletservice.dto.response.FeeResponse;
import kz.kbtu.walletservice.dto.response.TransferResponse;
import kz.kbtu.walletservice.dto.response.WalletResponse;
import kz.kbtu.walletservice.model.TransferResult;
import kz.kbtu.walletservice.model.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class WalletMapper {

    public WalletResponse toWalletResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getCurrency(),
                wallet.getBalance(),
                wallet.getStatus(),
                wallet.getCreatedAt()
        );
    }

    public List<WalletResponse> toWalletResponseList(List<Wallet> wallets) {
        List<WalletResponse> result = new ArrayList<>();
        for (Wallet wallet : wallets) {
            result.add(toWalletResponse(wallet));
        }
        return result;
    }

    public TransferResponse toTransferResponse(TransferResult transfer) {
        return new TransferResponse(
                transfer.from().getId(),
                transfer.to().getId(),
                transfer.from().getCurrency(),
                transfer.amount(),
                transfer.fee(),
                transfer.amount().add(transfer.fee()),
                transfer.from().getBalance()
        );
    }

    public FeeResponse toFeeResponse(BigDecimal amount, BigDecimal fee) {
        return new FeeResponse(amount, fee, amount.add(fee));
    }
}