package kz.kbtu.walletservice.web.dto.response;

import java.math.BigDecimal;

public record FeeResponse(
        BigDecimal amount,
        BigDecimal fee,
        BigDecimal total
) {}
