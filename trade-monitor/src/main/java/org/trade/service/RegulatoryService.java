package org.trade.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.common.model.RegulatoryReportDto;

import static org.springframework.http.HttpStatus.ACCEPTED;

@Service
public class RegulatoryService {

    public static final String REGULATORY_URL = "http://localhost:8082/regulatory/report";
    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(RegulatoryService.class);

    public RegulatoryService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    /**
     * Notifies the regulatory authority with the provided report data.
     *
     * @param dto the regulatory report data transfer object
     */
    public boolean notifyAuthority(RegulatoryReportDto dto) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    REGULATORY_URL,
                    dto,
                    String.class
            );

            if (response.getStatusCode() == ACCEPTED) {
                String responseBody = response.getBody();
                log.info("Authority acknowledged the report: " + responseBody);
                return true;
            } else {
                log.error("Unexpected status from authority: " + response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("Error notifying authority: " + e.getMessage());
            return false;
        }
    }

}

