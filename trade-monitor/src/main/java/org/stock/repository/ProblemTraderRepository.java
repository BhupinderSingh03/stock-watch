package org.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stock.entity.ProblemTrader;

@Repository
public interface ProblemTraderRepository extends JpaRepository<ProblemTrader, String> {
    boolean existsByTraderId(String traderId);
    ProblemTrader findByTraderId(String traderId);
}

