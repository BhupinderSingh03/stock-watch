package org.trade.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.trade.entity.ProblemTrader;
import org.trade.entity.TradeStore;
import org.common.model.RegulatoryReportDto;
import org.trade.model.TradeRequestDto;
import org.trade.repository.ProblemTraderRepository;
import org.trade.repository.TradeStoreRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.trade.contants.Constants.*;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeStoreRepository tradeStoreRepository;
    private final ProblemTraderRepository problemTraderRepository;
    private final RegulatoryService regulatoryService;

    private static final Logger log = LoggerFactory.getLogger(TradeService.class);


    /**
     * Checks if a trade is suspicious and processes it accordingly.
     *
     * @param requestDto the trade request data transfer object
     * @return ResponseEntity with the result of the trade check
     */
    public ResponseEntity checkSuspiciousTrade(TradeRequestDto requestDto) {
        Map<String, String> response = new HashMap<>();

        // Log the trade
        tradeStoreRepository.save(new TradeStore(
                requestDto.getUniqueTraderId(),
                requestDto.getUniqueStockId(),
                Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
                requestDto.getFirstName(),
                requestDto.getLastName(),
                requestDto.getNationalityCode(),
                requestDto.getCountryOfResidenceCode(),
                requestDto.getDateOfBirth(),
                requestDto.getAmount(),
                requestDto.getCurrency(),
                requestDto.getBuyOrSell()
        ));


        // Check if trader is already flagged
        // TODO: use redis  distributed cache for fast lookup
        if (problemTraderRepository.existsByTraderId(requestDto.getUniqueTraderId())) {
            log.warn("Trade rejected: {}", requestDto.getUniqueTraderId());
            response.put(MESSAGE, REJECTED_TRADE_FLAGGED);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        boolean isSuspicious = processTrade(requestDto);
        return suspiciousCheck(requestDto.getUniqueTraderId(), isSuspicious, response);
    }

    /**
     * Checks if the trade is suspicious and returns the appropriate response.
     *
     * @param uniqueTraderId the unique identifier of the trader
     * @param isSuspicious   whether the trade is suspicious
     * @param response       the response map to populate with messages
     * @return ResponseEntity with the result of the suspicious check
     */
    private ResponseEntity<Map<String, String>> suspiciousCheck(String uniqueTraderId, boolean isSuspicious, Map<String, String> response) {
        if (isSuspicious) {
            log.warn("Suspicious activity: {}", uniqueTraderId);
            response.put(MESSAGE, REJECTED_TRADE_REPORTED);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } else {
            response.put(MESSAGE, TRADE_ACCEPTED);
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Processes the trade request and checks for suspicious activity.
     *
     * @param request the trade request data transfer object
     * @return true if the trade is suspicious, false otherwise
     * @throws ResponseStatusException if any error occurs during processing
     */
    protected boolean processTrade(TradeRequestDto request) throws ResponseStatusException {
        boolean isSuspicious = false;
        LocalDateTime since = LocalDateTime.now().minusMinutes(10);

        // TODO: use redis  distributed cache for fast lookup
        long tradeCount = tradeStoreRepository.countByTraderIdAndStockIdAndTimestampGreaterThanEqual(
                request.getUniqueTraderId(),
                request.getUniqueStockId(),
                since
        );
        log.warn("trade count: {}", tradeCount);

        if (tradeCount > 5) {
            isSuspicious = true;
            // Log alert
            log.warn("Suspicious trading by trader '{}' on stock '{}'. Count: {}",
                    request.getUniqueTraderId(), request.getUniqueStockId(), tradeCount);

            boolean isNotified = false;
            try {
                //Save the flagged trader first
                problemTraderRepository.save(new ProblemTrader(
                        request.getUniqueTraderId(), LocalDateTime.now(), false // initially not notified
                ));

                // Now notify regulatory authority only after successful save
                RegulatoryReportDto report = RegulatoryReportDto.builder()
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .nationality(request.getNationalityCode())
                        .countryOfResidence(request.getCountryOfResidenceCode())
                        .dateOfBirth(request.getDateOfBirth())
                        .uniqueTraderId(request.getUniqueTraderId())
                        .uniqueStockId(request.getUniqueStockId())
                        .detectedAt(new Date())
                        .build();

                isNotified = regulatoryService.notifyAuthority(report);

                // Update the notified flag after successfully notified
                if (isNotified) {
                    // TODO: use redis  distributed cache for fast lookup
                    ProblemTrader flaggedTrader = problemTraderRepository.findByTraderId(request.getUniqueTraderId());
                    if (flaggedTrader != null) {
                        flaggedTrader.setNotifiedToRegulatoryAuthority(true);
                        problemTraderRepository.save(flaggedTrader);
                    }
                }

            } catch (DataIntegrityViolationException ex) {
                log.warn("Trader already flagged concurrently: {}", request.getUniqueTraderId());
                // Trader already flagged, no need to notify again
            }
        }
        return isSuspicious;
    }
}

