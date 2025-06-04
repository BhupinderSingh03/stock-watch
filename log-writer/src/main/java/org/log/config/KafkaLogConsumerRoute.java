package org.log.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.common.model.RegulatoryReportDto;
import org.springframework.stereotype.Component;

@Component
public class KafkaLogConsumerRoute extends RouteBuilder {

    /**
     * This route consumes messages from the Kafka topic "suspicious-traders",
     * unmarshals them into RegulatoryReportDto objects, logs the received reports,
     * and writes them to a file named "report-2025.log" in the "suspicious-reports" directory.
     */
    @Override
    public void configure() {
        from("kafka:suspicious-traders" +
                "?brokers=localhost:9092" +
                "&groupId=log-writer-groupv2" +
                "&autoOffsetReset=earliest" +
                "&keyDeserializer=org.apache.kafka.common.serialization.StringDeserializer" +
                "&valueDeserializer=org.apache.kafka.common.serialization.StringDeserializer")
                .unmarshal(get(RegulatoryReportDto.class))
                .log("Received suspicious trader report: ${body}")
                .marshal().json()
                .process(exchange -> {
                    String body = exchange.getIn().getBody(String.class);
                    exchange.getIn().setBody(body + System.lineSeparator());
                })
                .to("file:suspicious-reports?fileName=report-2025.log&fileExist=Append&autoCreate=true")
                .onException(Exception.class)
                .log("Error consuming message: ${exception.message}")
                .handled(true);
    }

    /**
     * Creates a JacksonDataFormat instance for the specified class type.
     * This is used to convert JSON messages to Java objects and vice versa.
     *
     * @param T the class type to be used for JSON conversion
     * @return a JacksonDataFormat instance configured with the specified class type
     */
    public static JacksonDataFormat get(final Class<?> T) {
        JacksonDataFormat jsonDataFormat = new JacksonDataFormat(T);
        jsonDataFormat.addModule(new JavaTimeModule());
        return jsonDataFormat;
    }
}
