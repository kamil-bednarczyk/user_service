package sa.common.config;

import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.serialization.Serializer;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class RabbitMQConfig {

    @Bean
    public Exchange exchange() {
        return ExchangeBuilder.fanoutExchange("events").build();
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable("events").build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("*").noargs();
    }

    @Autowired
    public void configure(AmqpAdmin admin) {
        admin.declareExchange(exchange());
        admin.declareQueue(queue());
        admin.declareBinding(binding());
    }

    @Bean
    public SpringAMQPMessageSource springAMQPMessageSource(Serializer serializer) {
        return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

            @Override
            @RabbitListener(queues = "events")
            public void onMessage(Message message, Channel channel) throws Exception {
                log.info("Received message: " + message.toString());
                super.onMessage(message, channel);
            }
        };
    }
}