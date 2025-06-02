package org.log.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.stereotype.Component;
import org.stock.model.RegulatoryReportDto;

@Component
public class KafkaLogConsumerRoute extends RouteBuilder {
    @Override
    public void configure() {
        from("kafka:suspicious-traders" +
                "?brokers=localhost:9092" +
                "&groupId=log-writer-groupv2" +
                "&autoOffsetReset=earliest" +
                "&keyDeserializer=org.apache.kafka.common.serialization.StringDeserializer" +
                "&valueDeserializer=org.apache.kafka.common.serialization.StringDeserializer")
                .unmarshal(get(RegulatoryReportDto.class))
                //.routeId("kafka-log-consumer")
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

    public static JacksonDataFormat get(final Class<?> T) {
        JacksonDataFormat jsonDataFormat = new JacksonDataFormat(T);
        jsonDataFormat.addModule(new JavaTimeModule());
        return jsonDataFormat;
    }
}

//
//@Component
//public class KafkaLogConsumerRoute extends RouteBuilder {
//
//    @Override
//    public void configure() throws Exception {
//        from("kafka:suspicious-traders?brokers=localhost:9092&groupId=log-writer-group")
//                .routeId("kafka-log-consumer")
//                .log("Received suspicious trader report: ${body}")
//                .to("file:logs/suspicious-reports?fileName=report-${date:now:yyyyMMddHHmmssSSS}.log&fileExist=Append");
//        ;
//    }
//}
