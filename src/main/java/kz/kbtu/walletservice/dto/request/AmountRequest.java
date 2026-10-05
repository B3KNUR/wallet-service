package kz.kbtu.walletservice.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AmountRequest (

        @NotNull(message = "{wallet.amount.required}")
        @Positive(message = "{wallet.amount.positive}")
        @Digits(integer = 15, fraction = 2, message = "{wallet.amount.digits}")
        BigDecimal amount
){}