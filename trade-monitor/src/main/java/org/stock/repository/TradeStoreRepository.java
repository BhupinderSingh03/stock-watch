package org.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stock.entity.TradeStore;

import java.time.LocalDateTime;

@Repository
public interface TradeStoreRepository extends JpaRepository<TradeStore, Long> {

//    @Query("""
//        SELECT COUNT(t) FROM TradeStore t
//        WHERE t.traderId = :traderId
//          AND t.stockId = :stockId
//          AND t.timestamp >= :since
//    """)
//    long countRecentTrades(
//            @Param("traderId") String traderId,
//            @Param("stockId") String stockId,
//            @Param("since") LocalDateTime since
//    );

    long countByTraderIdAndStockIdAndTimestampGreaterThanEqual(
            String traderId,
            String stockId,
            LocalDateTime since
    );
}

