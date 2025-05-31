package org.log.config;


import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaLogConsumerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("kafka:suspicious-traders?brokers=localhost:9092&groupId=log-writer-group")
                .routeId("kafka-log-consumer")
                .log("Received suspicious trader report: ${body}")
                .to("file:logs/suspicious-reports?fileName=report-${date:now:yyyyMMddHHmmssSSS}.log&fileExist=Append");
        ;
    }
}
