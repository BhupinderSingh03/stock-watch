package org.stock;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stock.model.TradeRequest;
import org.stock.service.TradeService;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }
    @PostMapping
    public ResponseEntity<String> createTrade(@Valid @RequestBody TradeRequest request) {
        tradeService.processTrade(request);
        return ResponseEntity.ok("Trade processed successfully");
    }
}

