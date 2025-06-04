package org.regulatory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.common.model.RegulatoryReportDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.regulatory.contants.Constants.*;

@RestController
@RequestMapping("/regulatory/report")
@RequiredArgsConstructor
public class RegulatoryReportController {

    private final ProducerTemplate producerTemplate;

    /**
     * Endpoint to receive regulatory reports and send them to a Kafka topic.
     *
     * @param report the regulatory report data transfer object
     * @return a message indicating the report was received and sent to Kafka
     */
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Map<String, String>> receiveReport(@RequestBody @Valid RegulatoryReportDto report) {
        producerTemplate.sendBody(ENDPOINT_SEND_TO_KAFKA, report);
        Map<String, String> response = new HashMap<>();
        response.put(MESSAGE, REGULATORY_RESPONSE);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}