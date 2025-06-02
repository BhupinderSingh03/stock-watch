package org.stock.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.stock.entity.ProblemTrader;
import org.stock.entity.TradeStore;
import org.stock.model.RegulatoryReportDto;
import org.stock.model.TradeRequestDto;
import org.stock.repository.ProblemTraderRepository;
import org.stock.repository.TradeStoreRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeStoreRepository tradeStoreRepository;
    private final ProblemTraderRepository problemTraderRepository;
    private final RegulatoryService regulatoryService;

    private static final Logger log = LoggerFactory.getLogger(TradeService.class);


    public ResponseEntity checkSuspiciousTrade(TradeRequestDto requestDto) {
        Map<String, String> response = new HashMap<>();

        // Log the trade
        tradeStoreRepository.save(new TradeStore(
                requestDto.getUniqueTraderId(),
                requestDto.getUniqueStockId(),
                LocalDateTime.now(),
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
        if (problemTraderRepository.existsByTraderId(requestDto.getUniqueTraderId())) {
            log.warn("Trade rejected: {}", requestDto.getUniqueTraderId());
            response.put("message", "Trade rejected: Trader is flagged as problem trader.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        boolean isSuspicious = processTrade(requestDto);
        return suspiciousCheck(requestDto.getUniqueTraderId(), isSuspicious, response);
    }

    private ResponseEntity<Map<String, String>> suspiciousCheck(String uniqueTraderId, boolean isSuspicious, Map<String, String> response) {
        if (isSuspicious) {
            log.warn("Suspicious activity: {}", uniqueTraderId);
            response.put("message", "Trade accepted. Trader flagged for suspicious activity and reported to the regulatory authority.");
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } else {
            response.put("message", "Trade accepted. No suspicious activity detected.");
            return ResponseEntity.ok(response);
        }
    }

    //use distributed redis cache for better performance and scalability
    @Transactional
    protected boolean processTrade(TradeRequestDto request) throws ResponseStatusException {
        //check this from database , cache and check multiple threads access too - pending
        boolean isSuspicious = false;

        // Check recent trades
        LocalDateTime since = LocalDateTime.now().minusMinutes(10);

        //database locking required here to ensure consistency - pending
        long tradeCount = tradeStoreRepository.countByTraderIdAndStockIdAndTimestampGreaterThanEqual(
                request.getUniqueTraderId(),
                request.getUniqueStockId(),
                since
        );

        log.warn("trade count: {}", tradeCount);


        //check concurrency issues here - pending
        if (tradeCount > 5) {
            isSuspicious  = true;
            // a) Log alert
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
                        .dateOfBirth(Date.from(request.getDateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                        .uniqueTraderId(request.getUniqueTraderId())
                        .uniqueStockId(request.getUniqueStockId())
                        .detectedAt(new Date())
                        .build();

                isNotified = regulatoryService.notifyAuthority(report);

                // Update the notified flag after successfully notified
                if (isNotified) {
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

