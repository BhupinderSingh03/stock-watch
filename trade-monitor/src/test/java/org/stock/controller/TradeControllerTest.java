package org.stock.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.trade.TradeMonitorApplication;
import org.trade.controller.TradeController;
import org.trade.model.TradeRequestDto;
import org.trade.service.TradeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.trade.contants.Constants.MESSAGE;
import static org.trade.model.BuyOrSell.BUY;

@WebMvcTest(TradeController.class)
@ContextConfiguration(classes = TradeMonitorApplication.class)
class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TradeService tradeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    LocalDate localDate = LocalDate.of(1991, 5, 13);
    Date date = Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());

    @Test
    void createTrade_ValidRequest_ReturnsOkResponse() throws Exception {
                TradeRequestDto validRequest = new TradeRequestDto("John", "Doe", "DE", "US",
                        date, "TRADER1234",
                new BigDecimal("1000.00"), "USD", "STOCK0w73091", BUY);
        Map<String, String> response = Map.of(MESSAGE, "Trade accepted. No suspicious activity detected.");
        ResponseEntity<Map<String, String>> responseEntity = ResponseEntity.ok(response);

        when(tradeService.checkSuspiciousTrade(any())).thenReturn(responseEntity);

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Trade accepted. No suspicious activity detected."));
    }

    @Test
    void createTrade_InvalidRequest_ReturnsBadRequest() throws Exception {
        TradeRequestDto inValidRequest = new TradeRequestDto("John", "Doe", "DE", "US",
                date, "",
                new BigDecimal("1000.00"), "USD", "STOCK0w73091", BUY);

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inValidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void createTrade_InvalidRequest_ReturnsInternalServerError() throws Exception {
        TradeRequestDto validRequest = new TradeRequestDto("John", "Doe", "DE", "US",
                date, "TRADER1234",
                new BigDecimal("1000.00"), "USD", "STOCK0w73091", BUY);
        Map<String, String> response = Map.of(MESSAGE, "Trade accepted. No suspicious activity detected.");
        ResponseEntity<Map<String, String>> responseEntity = ResponseEntity.ok(response);

        when(tradeService.checkSuspiciousTrade(validRequest)).thenThrow(new RuntimeException("Internal Server Error"));

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An unexpected error occurred. Please try again later."));
    }
}