package br.com.alexandreluchetti.cinealert.core.repository;

import br.com.alexandreluchetti.cinealert.dataprovider.entity.ContentEntity;

import java.util.Optional;

public interface ContentRepository {

    Optional<ContentEntity> findByImdbId(String imdbId);

    boolean existsByImdbId(String imdbId);

    Optional<ContentEntity> findById(String contentId);

    ContentEntity save(ContentEntity contentEntity);
}
