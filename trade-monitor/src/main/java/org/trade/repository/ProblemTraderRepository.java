package org.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.trade.entity.ProblemTrader;

@Repository
public interface ProblemTraderRepository extends JpaRepository<ProblemTrader, String> {
    boolean existsByTraderId(String traderId);
    ProblemTrader findByTraderId(String traderId);
}

