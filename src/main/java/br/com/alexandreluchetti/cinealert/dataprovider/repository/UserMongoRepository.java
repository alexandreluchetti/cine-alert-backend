package br.com.alexandreluchetti.cinealert.dataprovider.repository;

import br.com.alexandreluchetti.cinealert.dataprovider.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserMongoRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
