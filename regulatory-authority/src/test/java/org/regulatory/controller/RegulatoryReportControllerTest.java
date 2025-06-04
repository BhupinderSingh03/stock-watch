package org.regulatory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.common.model.RegulatoryReportDto;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegulatoryReportController.class)
class RegulatoryReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProducerTemplate producerTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    LocalDate localDate = LocalDate.of(1991, 5, 13);
    Date date = Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());

    RegulatoryReportDto reportDto = new RegulatoryReportDto("John", "Doe", "DE", "US",
            date, "TRADER1234", "STOCK0w73091", date);

    @Test
    void receiveReport_ValidReport_ReturnsAcceptedAndSendsToKafka() throws Exception {
        mockMvc.perform(post("/regulatory/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportDto)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("{\"message\":\"Report received and sent to Kafka topic.\"}"));

        ArgumentCaptor<RegulatoryReportDto> captor = ArgumentCaptor.forClass(RegulatoryReportDto.class);
        verify(producerTemplate).sendBody(Mockito.eq("direct:sendToKafka"), captor.capture());

        assertThat(captor.getValue()).isEqualTo(reportDto);
    }

    @Test
    void receiveReport_InvalidReport_ReturnsBadRequest() throws Exception {
        reportDto.setUniqueTraderId("");
        mockMvc.perform(post("/regulatory/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportDto)))
                .andExpect(status().isBadRequest());
    }
}
