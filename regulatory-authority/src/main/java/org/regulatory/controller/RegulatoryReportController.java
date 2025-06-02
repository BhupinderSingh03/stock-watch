package org.regulatory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.stock.model.RegulatoryReportDto;

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
    public String receiveReport(@RequestBody @Valid RegulatoryReportDto report) {
        producerTemplate.sendBody("direct:sendToKafka", report);
        return "Report received and sent to Kafka topic.";
    }
}