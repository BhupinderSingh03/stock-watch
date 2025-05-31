package org.stock.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class ProblemTrader {
    @Id
    private String id;
    private LocalDateTime flaggedAt;

    public ProblemTrader(String id, LocalDateTime flaggedAt) {
        this.id = id;
        this.flaggedAt = flaggedAt;
    }
}

