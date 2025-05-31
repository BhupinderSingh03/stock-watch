package org.stock.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.stock.model.BuyOrSell;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String traderId;
    @Column(nullable = false, unique = true)
    private String stockId;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String nationalityCode;
    @Column(nullable = false)
    private String countryOfResidenceCode;
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BuyOrSell buyOrSell;

    public TradeStore(String traderId, String stockId, LocalDateTime timestamp, String firstName, String lastName, String nationalityCode, String countryOfResidenceCode, LocalDate dateOfBirth, BigDecimal amount, String currency, BuyOrSell buyOrSell) {
        this.traderId = traderId;
        this.stockId = stockId;
        this.timestamp = timestamp;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalityCode = nationalityCode;
        this.countryOfResidenceCode = countryOfResidenceCode;
        this.dateOfBirth = dateOfBirth;
        this.amount = amount;
        this.currency = currency;
        this.buyOrSell = buyOrSell;
    }
}


