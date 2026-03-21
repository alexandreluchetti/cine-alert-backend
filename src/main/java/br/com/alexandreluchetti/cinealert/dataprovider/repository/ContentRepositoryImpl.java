package br.com.alexandreluchetti.cinealert.dataprovider.repository;

import br.com.alexandreluchetti.cinealert.core.model.Content;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentRepositoryImpl extends MongoRepository<Content, String> {

    Optional<Content> findByImdbId(String imdbId);

    boolean existsByImdbId(String imdbId);
}
