package org.trade.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.trade.model.BuyOrSell;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String traderId;
    @Column(nullable = false)
    private String stockId;
    @Column(nullable = false)
    private Date timestamp;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String nationalityCode;
    @Column(nullable = false)
    private String countryOfResidenceCode;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BuyOrSell buyOrSell;

    public TradeStore(String traderId, String stockId, Date timestamp, String firstName, String lastName, String nationalityCode, String countryOfResidenceCode, Date dateOfBirth, BigDecimal amount, String currency, BuyOrSell buyOrSell) {
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


