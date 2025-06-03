package org.stock.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.stock.model.RegulatoryReportDto;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegulatoryServiceTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RegulatoryService regulatoryService;



    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        regulatoryService = new RegulatoryService(restTemplateBuilder);
    }

    LocalDate localDate = LocalDate.of(1991, 5, 13);
    Date date = Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());

    RegulatoryReportDto reportDto = new RegulatoryReportDto("John", "Doe", "DE", "US",
            date, "TRADER1234", "STOCK0w73091", date);


    @Test
    void authorityAcknowledgesReportSuccessfully() {
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Acknowledged", HttpStatus.ACCEPTED);
        when(restTemplate.postForEntity(
                eq("http://localhost:8082/regulatory/report"),
                eq(reportDto),
                eq(String.class)
        )).thenReturn(mockResponse);

        boolean result = regulatoryService.notifyAuthority(reportDto);

        assertTrue(result);
    }

    @Test
    void authorityReturnsUnexpectedStatus() {
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Unexpected", HttpStatus.BAD_REQUEST);
        when(restTemplate.postForEntity(
                eq("http://localhost:8082/regulatory/report"),
                eq(reportDto),
                eq(String.class)
        )).thenReturn(mockResponse);

        boolean result = regulatoryService.notifyAuthority(reportDto);

        assertFalse(result);
    }

    @Test
    void exceptionOccursWhileNotifyingAuthority() {
        when(restTemplate.postForEntity(
                eq("http://localhost:8082/regulatory/report"),
                eq(reportDto),
                eq(String.class)
        )).thenThrow(new RuntimeException("Connection error"));

        boolean result = regulatoryService.notifyAuthority(reportDto);

        assertFalse(result);
    }
}