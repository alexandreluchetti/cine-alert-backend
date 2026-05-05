package br.com.alexandreluchetti.cinealert.dataprovider.repository;

import br.com.alexandreluchetti.cinealert.dataprovider.entity.ContentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ContentMongoRepository extends MongoRepository<ContentEntity, String> {

    Optional<ContentEntity> findFirstByImdbId(String imdbId);

    boolean existsByImdbId(String imdbId);
}
