package org.stock.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.trade.entity.ProblemTrader;
import org.common.model.RegulatoryReportDto;
import org.trade.model.TradeRequestDto;
import org.trade.repository.ProblemTraderRepository;
import org.trade.repository.TradeStoreRepository;
import org.trade.service.RegulatoryService;
import org.trade.service.TradeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.trade.model.BuyOrSell.BUY;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private TradeStoreRepository tradeStoreRepository;

    @Mock
    private ProblemTraderRepository problemTraderRepository;

    @Mock
    private RegulatoryService regulatoryService;

    @InjectMocks
    private TradeService tradeService;

    LocalDate localDate = LocalDate.of(1991, 5, 13);
    Date date = Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());

    TradeRequestDto validRequest = new TradeRequestDto("John", "Doe", "DE", "US",
            date, "TRADER1234",
            new BigDecimal("1000.00"), "USD", "STOCK0w73091", BUY);

    @Test
    void suspiciousTradeIsFlaggedAndReportedToRegulatoryAuthority() {
        Mockito.when(tradeStoreRepository.countByTraderIdAndStockIdAndTimestampGreaterThanEqual(
                Mockito.eq(validRequest.getUniqueTraderId()),
                Mockito.eq(validRequest.getUniqueStockId()),
                Mockito.any(LocalDateTime.class)
        )).thenReturn(6L);
        Mockito.when(problemTraderRepository.save(Mockito.any(ProblemTrader.class))).thenReturn(new ProblemTrader());
        Mockito.when(regulatoryService.notifyAuthority(Mockito.any(RegulatoryReportDto.class))).thenReturn(true);

        ResponseEntity<Map<String, String>> response = tradeService.checkSuspiciousTrade(validRequest);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Trade accepted. Trader flagged for suspicious activity and reported to the regulatory authority.", response.getBody().get("message"));
    }

    @Test
    void flaggedTraderTradeIsRejected() {
        Mockito.when(problemTraderRepository.existsByTraderId(validRequest.getUniqueTraderId())).thenReturn(true);

        ResponseEntity<Map<String, String>> response = tradeService.checkSuspiciousTrade(validRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Trade rejected: Trader is flagged as problem trader.", response.getBody().get("message"));
    }

    @Test
    void nonSuspiciousTradeIsAccepted() {
        Mockito.when(tradeStoreRepository.countByTraderIdAndStockIdAndTimestampGreaterThanEqual(
                Mockito.eq(validRequest.getUniqueTraderId()),
                Mockito.eq(validRequest.getUniqueStockId()),
                Mockito.any(LocalDateTime.class)
        )).thenReturn(2L);

       // Mockito.when(problemTraderRepository.save(Mockito.any(ProblemTrader.class))).thenReturn(new ProblemTrader());
        Mockito.when(problemTraderRepository.existsByTraderId(Mockito.eq(validRequest.getUniqueTraderId()))).thenReturn(false);


        ResponseEntity<Map<String, String>> response = tradeService.checkSuspiciousTrade(validRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Trade accepted. No suspicious activity detected.", response.getBody().get("message"));
    }

    @Test
    void concurrentFlaggingDoesNotDuplicateNotifications() {
        Mockito.when(tradeStoreRepository.countByTraderIdAndStockIdAndTimestampGreaterThanEqual(
                Mockito.eq(validRequest.getUniqueTraderId()),
                Mockito.eq(validRequest.getUniqueStockId()),
                Mockito.any(LocalDateTime.class)
        )).thenReturn(6L);
        Mockito.doThrow(DataIntegrityViolationException.class).when(problemTraderRepository).save(Mockito.any(ProblemTrader.class));

        ResponseEntity<Map<String, String>> response = tradeService.checkSuspiciousTrade(validRequest);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Trade accepted. Trader flagged for suspicious activity and reported to the regulatory authority.", response.getBody().get("message"));
    }
}