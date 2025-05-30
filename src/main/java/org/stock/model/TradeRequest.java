package org.stock.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for trade request")
public class TradeRequest {

    @NotBlank
    @JsonProperty("first_name")
    @Schema(example = "John", description = "First name of the trader")
    private String firstName;

    @NotBlank
    @JsonProperty("last_name")
    @Schema(example = "Doe", description = "Last name of the trader")
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}$", message = "Must be a valid ISO 3166-1 alpha-2 country code")
    @JsonProperty("nationality")
    @Schema(example = "DE", description = "ISO 3166-1 alpha-2 code for nationality")
    private String nationalityCode;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}$", message = "Must be a valid ISO 3166-1 alpha-2 country code")
    @JsonProperty("country_of_residence")
    @Schema(example = "US", description = "ISO 3166-1 alpha-2 code for country of residence")
    private String countryOfResidenceCode;

    @NotNull
    @JsonProperty("date_of_birth")
    @Schema(example = "1991-05-13", description = "Date of birth in YYYY-MM-DD format")
    private LocalDate dateOfBirth;

    @NotBlank
    @JsonProperty("unique_trader_id")
    @Schema(example = "TRADER1234", description = "Unique identifier for the trader")
    private String uniqueTraderId;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @JsonProperty("amount")
    @Schema(example = "1000.00", description = "Transaction amount")
    private BigDecimal amount;

    @NotBlank
    @Size(min = 3, max = 3)
    @JsonProperty("currency")
    @Schema(example = "USD", description = "Three-letter currency code (ISO 4217)")
    private String currency;

    @NotBlank
    @JsonProperty("unique_stock_id")
    @Schema(example = "STOCK5678", description = "Unique identifier for the stock")
    private String uniqueStockId;

    @NotNull
    @JsonProperty("buy_or_sell")
    @Schema(example = "BUY", description = "Transaction type: BUY or SELL")
    private BuyOrSell buyOrSell;
}