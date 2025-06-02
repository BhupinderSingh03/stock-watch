package org.stock.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.stock.model.RegulatoryReportDto;

@Service
public class RegulatoryService {

    private final RestTemplate restTemplate;

    public RegulatoryService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    /**
     * Notifies the regulatory authority with the provided report data.
     *
     * @param dto the regulatory report data transfer object
     */
    public void notifyAuthority(RegulatoryReportDto dto) {
        restTemplate.postForEntity("https://localhost:8080/regulatory/report", dto, RegulatoryReportDto.class);
    }
}

