package org.stock.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stock.model.TradeRequestDto;
import org.stock.service.TradeService;

import java.util.Map;

@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;
    /**
     * Endpoint to process a trade request.
     *
     * @param requestDto the trade request data transfer object
     * @return a response entity indicating the result of the operation
     */

    @PostMapping
    public ResponseEntity<Map<String, String>> createTrade(@Valid @RequestBody TradeRequestDto requestDto) {
       return tradeService.checkSuspiciousTrade(requestDto);
    }
}

