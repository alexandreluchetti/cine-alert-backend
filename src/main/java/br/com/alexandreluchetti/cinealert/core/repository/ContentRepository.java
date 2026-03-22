package br.com.alexandreluchetti.cinealert.core.repository;

import br.com.alexandreluchetti.cinealert.dataprovider.entity.ContentEntity;
import br.com.alexandreluchetti.cinealert.dataprovider.repository.ContentRepositoryImpl;

import java.util.Optional;

public interface ContentRepository extends ContentRepositoryImpl {

    Optional<ContentEntity> findByImdbId(String imdbId);

    boolean existsByImdbId(String imdbId);
}
