package br.com.alexandreluchetti.cinealert.dataprovider.repository;

import br.com.alexandreluchetti.cinealert.dataprovider.entity.ContentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentRepositoryImpl extends MongoRepository<ContentEntity, String> {

    Optional<ContentEntity> findByImdbId(String imdbId);

    boolean existsByImdbId(String imdbId);
}
