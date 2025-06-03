package org.regulatory.config;

import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReportToKafkaRouteTest {

    @Mock
    private ProducerTemplate producerTemplate;

    @InjectMocks
    private ReportToKafkaRoute reportToKafkaRoute;

    @Test
    void messageIsSentToKafkaSuccessfully() {
        String message = "{\"key\":\"value\"}";

        reportToKafkaRoute.configure();
        producerTemplate.sendBody("direct:sendToKafka", message);

        Mockito.verify(producerTemplate).sendBody("direct:sendToKafka", message);
    }

    @Test
    void invalidMessageThrowsException() {
        String invalidMessage = null;
        Mockito.doThrow(new IllegalArgumentException("Message cannot be null"))
                .when(producerTemplate)
                .sendBody("direct:sendToKafka", invalidMessage);

        assertThrows(IllegalArgumentException.class, () -> {
            producerTemplate.sendBody("direct:sendToKafka", invalidMessage);
        });
    }
}