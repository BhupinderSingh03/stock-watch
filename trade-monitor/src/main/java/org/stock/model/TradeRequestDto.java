package org.stock.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for trade request")
public class TradeRequestDto {

    @NotBlank(message = "first_name is required")
    @JsonProperty("first_name")
    @Schema(example = "John", description = "First name of the trader")
    private String firstName;

    @NotBlank(message = "last_name is required")
    @JsonProperty("last_name")
    @Schema(example = "Doe", description = "Last name of the trader")
    private String lastName;

    @NotBlank(message = "nationality is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Must be a valid ISO 3166-1 alpha-2 country code")
    @JsonProperty("nationality")
    @Schema(example = "DE", description = "ISO 3166-1 alpha-2 code for nationality")
    private String nationalityCode;

    @NotBlank(message = "country_of_residence is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Must be a valid ISO 3166-1 alpha-2 country code")
    @JsonProperty("country_of_residence")
    @Schema(example = "US", description = "ISO 3166-1 alpha-2 code for country of residence")
    private String countryOfResidenceCode;

    @JsonProperty("date_of_birth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(example = "1991-05-13", description = "Date of birth in YYYY-MM-DD format")
    private Date dateOfBirth;

    @NotBlank(message = "unique_trader_id is required")
    @JsonProperty("unique_trader_id")
    @Schema(example = "TRADER1234", description = "Unique identifier for the trader")
    private String uniqueTraderId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @JsonProperty("amount")
    @Schema(example = "1000.00", description = "Transaction amount")
    private BigDecimal amount;

    @NotBlank(message = "currency is required")
    @Size(min = 3, max = 3)
    @JsonProperty("currency")
    @Schema(example = "USD", description = "Three-letter currency code (ISO 4217)")
    private String currency;

    @NotBlank(message = "unique_stock_id is required")
    @JsonProperty("unique_stock_id")
    @Schema(example = "STOCK5678", description = "Unique identifier for the stock")
    private String uniqueStockId;

    @NotNull(message = "buy_or_sell is required")
    @JsonProperty("buy_or_sell")
    @Schema(example = "BUY", description = "Transaction type: BUY or SELL")
    private BuyOrSell buyOrSell;
}