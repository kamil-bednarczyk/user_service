package sa.common.kafka;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class KafkaReceiver {

    @KafkaListener(topics = "${kafka.topic.userCreated}")
    public void receive(ConsumerRecord<String, String> record) {
        log.info("received payload='{}'", record.toString());
    }
}