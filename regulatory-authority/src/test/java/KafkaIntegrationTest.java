//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.test.EmbeddedKafkaBroker;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.test.utils.KafkaTestUtils;
//
//import java.util.Map;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@SpringBootTest
//@EmbeddedKafka(partitions = 1, topics = { "test-topic" }, brokerProperties = {
//        "listeners=PLAINTEXT://localhost:9092", "port=9092"
//})
////@EmbeddedKafka(partitions = 1, topics = {"suspicious-traders"})
//public class KafkaIntegrationTest {
//
//    private KafkaTemplate<String, String> kafkaTemplate;
//    private EmbeddedKafkaBroker embeddedKafkaBroker;
//
//    public KafkaIntegrationTest(KafkaTemplate<String, String> kafkaTemplate, EmbeddedKafkaBroker embeddedKafkaBroker) {
//        this.kafkaTemplate = kafkaTemplate;
//        this.embeddedKafkaBroker = embeddedKafkaBroker;
//    }
//
//    @Test
//    void testSendMessage() {
//        kafkaTemplate.send("suspicious-traders", "key1", "{\"name\":\"John\"}");
//        // Add assertions or consumers to verify message reception
//
//
//        // Create a test consumer to verify the message was received
//        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
//                "testGroup", "true", embeddedKafkaBroker
//        );
//        try (var consumer = new DefaultKafkaConsumerFactory<String, String>(consumerProps)
//                .createConsumer()) {
//
//            embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "test-topic");
//
//            ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "test-topic");
//
//            assertThat(record).isNotNull();
//            assertThat(record.value()).isEqualTo("test-message");
//        }
//    }
//}
