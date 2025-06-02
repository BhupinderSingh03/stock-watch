package org.regulatory.config;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ReportToKafkaRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:sendToKafka")
                .routeId("regulatory-report-to-kafka")
                .marshal().json()  // convert object to JSON string
                .to("kafka:suspicious-traders?brokers={{spring.kafka.bootstrap-servers}}");
    }
}
