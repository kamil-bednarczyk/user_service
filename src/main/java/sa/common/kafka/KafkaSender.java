package sa.common.kafka;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import sa.common.service.UserDto;

@Component
@Log4j2
public class KafkaSender {

    private final KafkaTemplate<String, UserDto> kafkaTemplate;

    @Autowired
    public KafkaSender(KafkaTemplate<String, UserDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void send(String topic, UserDto payload) {
        log.info("Sending: " + payload.toString() + " to topic:" + topic);
        kafkaTemplate.send(topic, payload);
    }
}
