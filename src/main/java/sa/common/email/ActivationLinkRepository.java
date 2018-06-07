package sa.common.email;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivationLinkRepository extends MongoRepository< ActivationLink ,String> {
}
