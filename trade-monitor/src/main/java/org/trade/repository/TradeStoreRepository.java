package org.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.trade.entity.TradeStore;

import java.time.LocalDateTime;

@Repository
public interface TradeStoreRepository extends JpaRepository<TradeStore, Long> {

    long countByTraderIdAndStockIdAndTimestampGreaterThanEqual(
            String traderId,
            String stockId,
            LocalDateTime since
    );
}

