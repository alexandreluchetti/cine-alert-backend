package br.com.alexandreluchetti.cinealert.core.repository;

import br.com.alexandreluchetti.cinealert.core.model.Content;
import br.com.alexandreluchetti.cinealert.dataprovider.repository.ContentRepositoryImpl;

import java.util.Optional;

public interface ContentRepository extends ContentRepositoryImpl {

    Optional<Content> findByImdbId(String imdbId);

    boolean existsByImdbId(String imdbId);
}
