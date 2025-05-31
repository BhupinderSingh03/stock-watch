package org.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "RegulatoryReport")
public class RegulatoryReportDto {

    @JsonProperty("first_name")
    @XmlElement(name = "FirstName")
    private String firstName;

    @JsonProperty("last_name")
    @XmlElement(name = "LastName")
    private String lastName;

    @JsonProperty("nationality")
    @XmlElement(name = "Nationality")
    private String nationality; // ISO 3166-1 alpha-2

    @JsonProperty("country_of_residence")
    @XmlElement(name = "CountryOfResidence")
    private String countryOfResidence; // ISO 3166-1 alpha-2

    @JsonProperty("date_of_birth")
    @XmlElement(name = "DateOfBirth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String dateOfBirth;

    @JsonProperty("unique_trader_id")
    @XmlElement(name = "UniqueTraderID")
    private String uniqueTraderId;

    @JsonProperty("unique_stock_id")
    @XmlElement(name = "UniqueStockID")
    private String uniqueStockId;

    @JsonProperty("detected_at")
    @XmlElement(name = "DetectedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime detectedAt;
}

