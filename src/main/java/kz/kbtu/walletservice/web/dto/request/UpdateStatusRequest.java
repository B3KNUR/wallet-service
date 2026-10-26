package kz.kbtu.walletservice.web.dto.request;

import jakarta.validation.constraints.NotNull;
import kz.kbtu.walletservice.domain.WalletStatus;

public record UpdateStatusRequest(

        @NotNull(message = "{wallet.status.required}")
        WalletStatus status
) {}