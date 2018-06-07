package sa.common.config;

import lombok.extern.log4j.Log4j2;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.mongo.MongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
public class AxonConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public EventStorageEngine eventStorageEngine() {
        return new MongoEventStorageEngine(mongoTemplate);
    }

    @Bean
    public EventStore eventStore() {
        return new EmbeddedEventStore(eventStorageEngine());
    }
}