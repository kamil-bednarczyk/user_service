package sa.common.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sa.common.model.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
}
