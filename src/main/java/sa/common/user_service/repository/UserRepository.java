package sa.common.user_service.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import sa.common.user_service.model.User;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findById(String id);

    Mono<User> findByUsername(String username);
}
