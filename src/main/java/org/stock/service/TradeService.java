package org.stock.service;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.stock.entity.ProblemTrader;
import org.stock.entity.RegulatoryReportDto;
import org.stock.entity.TradeLog;
import org.stock.exceptions.NonUniqueIdException;
import org.stock.model.TradeRequest;
import org.stock.repository.ProblemTraderRepository;
import org.stock.repository.TradeLogRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class TradeService {

    private final TradeLogRepository tradeLogRepository;
    private final ProblemTraderRepository problemTraderRepository;
    private final RegulatoryService regulatoryService;

    public TradeService(
            TradeLogRepository tradeLogRepository,
            ProblemTraderRepository problemTraderRepository,
            RegulatoryService regulatoryService
    ) {
        this.tradeLogRepository = tradeLogRepository;
        this.problemTraderRepository = problemTraderRepository;
        this.regulatoryService = regulatoryService;
    }

    private static final Logger log = LoggerFactory.getLogger(TradeService.class);

    private final Set<String> traderIds = new HashSet<>();
    private final Set<String> stockIds = new HashSet<>();

    public void processTrade(@Valid TradeRequest request) {
        if (!traderIds.add(request.getUniqueTraderId())) {
            throw new NonUniqueIdException("Trader ID '" + request.getUniqueTraderId() + "' already exists.");
        }

        if (!stockIds.add(request.getUniqueStockId())) {
            throw new NonUniqueIdException("Stock ID '" + request.getUniqueStockId() + "' already exists.");
        }

// Log the trade
        tradeLogRepository.save(new TradeLog(
                request.getUniqueTraderId(),
                request.getUniqueStockId(),
                request.getBuyOrSell(),
                LocalDateTime.now()
        ));

        // Check recent trades
        LocalDateTime since = LocalDateTime.now().minusMinutes(10);
        long tradeCount = tradeLogRepository.countByTraderIdAndStockIdAndTimestampGreaterThanEqual(
                request.getUniqueTraderId(),
                request.getUniqueStockId(),
                since
        );

        if (tradeCount > 5) {
            // a) Log alert
            log.warn("Suspicious trading: {} trades by trader {} on stock {}",
                    tradeCount, request.getUniqueTraderId(), request.getUniqueStockId());

            // b) Flag trader
            problemTraderRepository.save(new ProblemTrader(
                    request.getUniqueTraderId(), LocalDateTime.now()
            ));

            // c) Notify authority
//            regulatoryService.notifyAuthority(
//                    request.getUniqueTraderId(),
//                    request.getUniqueStockId()
//            );

            RegulatoryReportDto report = RegulatoryReportDto.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .nationality(request.getNationalityCode())
                    .countryOfResidence(request.getCountryOfResidenceCode())
                    .dateOfBirth(request.getDateOfBirth())
                    .uniqueTraderId(request.getUniqueTraderId())
                    .uniqueStockId(request.getUniqueStockId())
                    .detectedAt(LocalDateTime.now())
                    .build();

            regulatoryService.notifyAuthorityAsJson(report); // or notifyAuthorityAsXml(report);
        }
    }



}

