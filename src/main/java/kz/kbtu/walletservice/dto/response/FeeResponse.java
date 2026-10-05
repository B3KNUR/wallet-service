package kz.kbtu.walletservice.dto.response;

import java.math.BigDecimal;

public record FeeResponse(
        BigDecimal amount,
        BigDecimal fee,
        BigDecimal total
) {}
