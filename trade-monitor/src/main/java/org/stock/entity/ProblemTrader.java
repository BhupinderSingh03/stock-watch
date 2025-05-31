package org.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "problem_trader")
@Data
@NoArgsConstructor
public class ProblemTrader {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "flagged_at")
    private LocalDateTime flaggedAt;

    public ProblemTrader(String id, LocalDateTime flaggedAt) {
        this.id = id;
        this.flaggedAt = flaggedAt;
    }
}

