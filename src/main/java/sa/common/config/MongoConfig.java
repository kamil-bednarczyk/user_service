package sa.common.config;

import com.mongodb.MongoClient;
import org.axonframework.mongo.DefaultMongoTemplate;
import org.axonframework.mongo.MongoTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        return new MongoClient("localhost", 27017);
    }

    @Bean
    public MongoTemplate defaultMongoTemplate() {
        return new DefaultMongoTemplate(mongoClient());
    }
}