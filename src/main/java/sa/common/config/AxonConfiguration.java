package sa.common.config;

import com.mongodb.MongoClient;
import lombok.extern.log4j.Log4j2;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.mongo.DefaultMongoTemplate;
import org.axonframework.mongo.MongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfiguration {

    private final String host;
    private final Integer port;

    public AxonConfiguration(@Value("${spring.data.mongodb.host}") String host,
                              @Value("${spring.data.mongodb.port}") Integer port) {
        this.host = host;
        this.port = port;
    }

    @Bean
    public MongoClient mongoClient() {
        return new MongoClient(host, port);
    }

    @Bean
    public MongoTemplate defaultMongoTemplate() {
        return new DefaultMongoTemplate(mongoClient());
    }

    @Bean
    public EventStorageEngine eventStorageEngine() {
        return new MongoEventStorageEngine(defaultMongoTemplate());
    }

    @Bean
    public EventStore eventStore() {
        return new EmbeddedEventStore(eventStorageEngine());
    }
}