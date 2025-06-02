package org.stock.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class TradeService {

    private final TradeStoreRepository tradeStoreRepository;
    private final ProblemTraderRepository problemTraderRepository;
    private final RegulatoryService regulatoryService;

    /**
     * Constructor for TradeService.
     *
     * @param tradeStoreRepository      the repository for trade logs
     * @param problemTraderRepository the repository for problem traders
     * @param regulatoryService       the service for regulatory notifications
     */
    public TradeService(
            TradeStoreRepository tradeStoreRepository,
            ProblemTraderRepository problemTraderRepository,
            RegulatoryService regulatoryService
    ) {
        this.tradeStoreRepository = tradeStoreRepository;
        this.problemTraderRepository = problemTraderRepository;
        this.regulatoryService = regulatoryService;
    }

    private static final Logger log = LoggerFactory.getLogger(TradeService.class);


    //use distributed redis cache for better performance and scalability
    public void processTrade(TradeRequestDto request) throws ResponseStatusException {
        //check this from database , cache and check multiple threads access too - pending

// Log the trade
        tradeStoreRepository.save(new TradeStore(
                request.getUniqueTraderId(),
                request.getUniqueStockId(),
                LocalDateTime.now(),
                request.getFirstName(),
                request.getLastName(),
                request.getNationalityCode(),
                request.getCountryOfResidenceCode(),
                request.getDateOfBirth(),
                request.getAmount(),
                request.getCurrency(),
                request.getBuyOrSell()
        ));

        // Check recent trades
        LocalDateTime since = LocalDateTime.now().minusMinutes(10);

        //database locking required here to ensure consistency - pending
        long tradeCount = tradeStoreRepository.countByTraderIdAndStockIdAndTimestampGreaterThanEqual(
                request.getUniqueTraderId(),
                request.getUniqueStockId(),
                since
        );
        log.warn("testing trade count: {}", tradeCount);


        //check concurrency issues here - pending
        if (tradeCount > 5) {
            // a) Log alert
            log.warn("Suspicious trading by trader '{}' on stock '{}'. Count: {}",
                    request.getUniqueTraderId(), request.getUniqueStockId(), tradeCount);

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
                    .dateOfBirth(Date.from(request.getDateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .uniqueTraderId(request.getUniqueTraderId())
                    .uniqueStockId(request.getUniqueStockId())
                    .detectedAt(new Date())
                    .build();

            regulatoryService.notifyAuthority(report); // or notifyAuthorityAsXml(report);
        }
    }



}

