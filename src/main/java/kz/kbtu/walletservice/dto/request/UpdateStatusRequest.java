package kz.kbtu.walletservice.dto.request;

import jakarta.validation.constraints.NotNull;
import kz.kbtu.walletservice.enums.WalletStatus;

public record UpdateStatusRequest(

        @NotNull(message = "{wallet.status.required}")
        WalletStatus status
) {}