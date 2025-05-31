package org.stock.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.stock.model.BuyOrSell;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class TradeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String traderId;
    private String stockId;
    @Enumerated(EnumType.STRING)
    private BuyOrSell buyOrSell;
    private LocalDateTime timestamp;

    public TradeLog(String traderId, String stockId, BuyOrSell buyOrSell, LocalDateTime timestamp) {
        this.traderId = traderId;
        this.stockId = stockId;
        this.buyOrSell = buyOrSell;
        this.timestamp = timestamp;
    }
}


