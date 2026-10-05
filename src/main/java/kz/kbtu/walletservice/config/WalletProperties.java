package kz.kbtu.walletservice.config;

import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "wallet")
public record WalletProperties(
        @NotBlank String defaultCurrency,
        @NotNull @Positive BigDecimal maxTransferLimit,
        @NotEmpty List<String> allowedCurrencies,
        @NotNull @DecimalMin("0.0") @DecimalMax("1.0") BigDecimal commissionFee,
        Boolean commissionEnabled
) {}