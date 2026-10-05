package kz.kbtu.walletservice.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(

        @NotNull(message = "{wallet.transfer.fromWalletId.required}")
        @Positive(message = "{wallet.transfer.fromWalletId.positive}")
        Long fromWalletId,

        @NotNull(message = "{wallet.transfer.toWalletId.required}")
        @Positive(message = "{wallet.transfer.toWalletId.positive}")
        Long toWalletId,

        @NotNull(message = "{wallet.transfer.amount.required}")
        @Positive(message = "{wallet.transfer.amount.positive}")
        @Digits(integer = 15, fraction = 2, message = "{wallet.transfer.amount.digits}")
        BigDecimal amount
) {}