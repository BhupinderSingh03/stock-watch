package org.stock.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//document the schema for the regulatory report
public class RegulatoryReportDto {

    @NotBlank(message = "first_name is required")
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank(message = "last_name is required")
    @JsonProperty("last_name")
    private String lastName;

    @NotBlank(message = "nationality is required")
    @JsonProperty("nationality")
    private String nationality; // ISO 3166-1 alpha-2

    @NotBlank(message = "country_of_residence is required")
    @JsonProperty("country_of_residence")
    private String countryOfResidence; // ISO 3166-1 alpha-2

    @NotBlank(message = "dateOfBirth is required")
    @JsonProperty("date_of_birth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotBlank(message = "unique_trader_id is required")
    @JsonProperty("unique_trader_id")
    private String uniqueTraderId;

    @NotBlank(message = "unique_stock_id is required")
    @JsonProperty("unique_stock_id")
    private String uniqueStockId;

    @NotNull(message = "Timestamp is mandatory")
    @JsonProperty("detected_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime detectedAt;
}

