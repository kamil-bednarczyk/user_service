package sa.common.config;

import com.mongodb.MongoClient;
import org.axonframework.mongo.DefaultMongoTemplate;
import org.axonframework.mongo.MongoTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfiguration {

    private final String host;
    private final Integer port;

    public MongoConfiguration(@Value("${spring.data.mongodb.host}") String host,
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
}