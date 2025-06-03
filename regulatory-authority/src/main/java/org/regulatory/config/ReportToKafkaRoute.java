package org.regulatory.config;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ReportToKafkaRoute extends RouteBuilder {

    /**
     * This route sends regulatory reports to a Kafka topic.
     * It listens for messages on the "direct:sendToKafka" endpoint,
     * marshals the message to JSON format, and sends it to the
     * "suspicious-traders" Kafka topic.
     */
    @Override
    public void configure() {
        from("direct:sendToKafka")
                .routeId("regulatory-report-to-kafka")
                .marshal().json()  // convert object to JSON string
                .to("kafka:suspicious-traders?brokers={{spring.kafka.bootstrap-servers}}");
    }
}
