package org.trade.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "problem_trader")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemTrader {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trader_id", nullable = false, unique = true)
    private String traderId;

    @Column(name = "flagged_at", nullable = false)
    private LocalDateTime flaggedAt;

    @Column(name = "notified", nullable = false)
    private Boolean notifiedToRegulatoryAuthority = false;

    public ProblemTrader(String traderId, LocalDateTime flaggedAt, Boolean notifiedToRegulatoryAuthority) {
        this.traderId = traderId;
        this.flaggedAt = flaggedAt;
        this.notifiedToRegulatoryAuthority = notifiedToRegulatoryAuthority;
    }
}

