package org.log.config;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stock.model.RegulatoryReportDto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class KafkaLogConsumerRouteTest {

    @Mock
    private ProducerTemplate producerTemplate;

    @InjectMocks
    private KafkaLogConsumerRoute kafkaLogConsumerRoute;

    @Test
    void messageIsConsumedAndWrittenToFile() throws Exception {
        LocalDate localDate = LocalDate.of(1991, 5, 13);
        Date date = Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());

        RegulatoryReportDto reportDto = new RegulatoryReportDto("John", "Doe", "DE", "US",
                date, "TRADER1234", "STOCK0w73091", date);

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(reportDto);

        kafkaLogConsumerRoute.configure();
        producerTemplate.send("direct:start", exchange);

        String expectedLogEntry = "{\"first_name\":\"John\",\"last_name\":\"Doe\",\"nationality\":\"AB\",\"country_of_residence\":\"US\",\"date_of_birth\":\"1991-05-13\",\"unique_trader_id\":\"TRADER2024d41\",\"unique_stock_id\":\"STOCK0w73091\",\"detected_at\":\"2025-06-03T20:10:17\"}";
        Path logFilePath = Paths.get("suspicious-reports/report-2025.log");
        System.out.println(Files.readString(logFilePath));
        assertTrue(Files.readString(logFilePath).contains(expectedLogEntry));
    }

    @Test
    void invalidMessageLogsErrorAndIsHandled() throws Exception {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody("Invalid message");

        kafkaLogConsumerRoute.configure();
        producerTemplate.send("direct:start", exchange);

        Mockito.verify(producerTemplate, Mockito.never()).sendBody(Mockito.anyString(), Mockito.any());
    }
}