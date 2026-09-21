package kz.kbtu.walletservice;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "wallet")
public record WalletProperties(
        @NotBlank String defaultCurrency,
        @Min(1) Long maxTransferLimit,
        @NotEmpty List<String> allowedCurrencies,
        Double commissionFee,
        Boolean commissionEnabled
) {}