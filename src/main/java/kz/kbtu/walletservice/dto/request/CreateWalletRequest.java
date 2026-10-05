package kz.kbtu.walletservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record CreateWalletRequest(

        @NotNull(message = "{wallet.userId.required}")
        @Positive(message = "{wallet.userId.positive}")
        Long userId,

        @NotBlank(message = "{wallet.currency.required}")
        @Pattern(regexp = "^[A-Za-z]{3}$", message = "{wallet.currency.pattern}")
        String currency
) {}
