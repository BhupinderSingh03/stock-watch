package org.trade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trade.model.TradeRequestDto;
import org.trade.service.TradeService;

import java.util.Map;

@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
@Tag(name = "Trade", description = "suspicious activity checking")
public class TradeController {

    private final TradeService tradeService;

    /**
     * Checks for suspicious activity.
     *
     * @param requestDto the trade request data transfer object containing trade details
     *                   (validated using Jakarta Bean Validation annotations)
     * @return a ResponseEntity containing a map with a message about the trade status
     */
    @Operation(
            summary = "Check for suspicious activity",
            description = "Accepts trade details and returns a message indicating if the trade is suspicious or not.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Trade request payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TradeRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trade accepted. No suspicious activity detected.",
                            content = @Content(schema = @Schema(implementation = java.util.Map.class))
                    ),
                    @ApiResponse(responseCode = "202", description = "{\n" +
                            "    \"message\": \"Trade accepted. Trader flagged for suspicious activity and reported to the regulatory authority.\"\n" +
                            "}\n",
                            content = @Content(schema = @Schema(implementation = java.util.Map.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "{\n" +
                            "                              \"errors\": {\n" +
                            "                                \"fieldName\": \"error message\"\n" +
                            "                              }\n" +
                            "                            }"),
                    @ApiResponse(responseCode = "500", description = "An unexpected error occurred. Please try again later."),
                    @ApiResponse(responseCode = "403", description = "{\n" +
                            "    \"message\": \"Trade rejected: Trader is flagged as problem trader.\"\n" +
                            "}",
                            content = @Content(schema = @Schema(implementation = java.util.Map.class))
                    ),
            }
    )
    @PostMapping
    public ResponseEntity<Map<String, String>> createTrade(@Valid @RequestBody TradeRequestDto requestDto) {
        return tradeService.checkSuspiciousTrade(requestDto);
    }
}

