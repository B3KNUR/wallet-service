package kz.kbtu.walletservice.controller;

import kz.kbtu.walletservice.domain.WalletStatus;
import kz.kbtu.walletservice.domain.exception.InsufficientFundsException;
import kz.kbtu.walletservice.domain.exception.WalletNotFoundException;
import kz.kbtu.walletservice.web.mapper.WalletMapper;
import kz.kbtu.walletservice.domain.Wallet;
import kz.kbtu.walletservice.app.WalletService;
import kz.kbtu.walletservice.web.controller.WalletController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
@Import(WalletMapper.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;

    private Wallet walletWithId(Long id, String currency) {
        return new Wallet(id, 1L, currency, BigDecimal.ZERO, WalletStatus.ACTIVE, Instant.now());
    }

    @Test
    void createWallet_returns201AndLocation() throws Exception {
        when(walletService.create(1L, "KZT")).thenReturn(walletWithId(1L, "KZT"));

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1, \"currency\": \"KZT\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/wallets/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.currency").value("KZT"));
    }

    @Test
    void createWallet_withoutCurrency_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("currency: currency is required"));

        verify(walletService, never()).create(any(), any());
    }

    @Test
    void getWallet_returns200() throws Exception {
        when(walletService.getById(1L)).thenReturn(walletWithId(1L, "KZT"));

        mockMvc.perform(get("/api/v1/wallets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getWallet_whenNotFound_returns404() throws Exception {
        when(walletService.getById(99L)).thenThrow(new WalletNotFoundException(99L));

        mockMvc.perform(get("/api/v1/wallets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not found"));
    }

    @Test
    void withdraw_whenNotEnoughMoney_returns409() throws Exception {
        when(walletService.withdraw(eq(1L), any(BigDecimal.class)))
                .thenThrow(new InsufficientFundsException(1L, new BigDecimal("100"), new BigDecimal("500")));

        mockMvc.perform(post("/api/v1/wallets/1/withdrawals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 500}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"));
    }
}