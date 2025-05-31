package org.stock.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.stock.entity.RegulatoryReportDto;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class RegulatoryService {

    private final RestTemplate restTemplate;

    public RegulatoryService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }
    public void notifyAuthority(String traderId, String stockId) {
        // Construct payload
        Map<String, String> payload = Map.of(
                "traderId", traderId,
                "stockId", stockId,
                "timestamp", LocalDateTime.now().toString()
        );

        restTemplate.postForEntity(
                "https://regulatory-authority.gov/alerts",
                payload,
                Void.class
        );
    }

    public void notifyAuthorityAsJson(RegulatoryReportDto dto) {
        restTemplate.postForEntity("https://regulator.example.com/api/report", dto, Void.class);
    }

    public void notifyAuthorityAsXml(RegulatoryReportDto dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<RegulatoryReportDto> request = new HttpEntity<>(dto, headers);

        restTemplate.postForEntity("https://regulator.example.com/api/report/xml", request, Void.class);
    }
}

