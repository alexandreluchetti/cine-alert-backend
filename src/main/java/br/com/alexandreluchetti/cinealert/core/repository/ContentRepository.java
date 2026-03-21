package br.com.alexandreluchetti.cinealert.core.repository;

import br.com.alexandreluchetti.cinealert.core.model.Content;

import java.util.Optional;

public interface ContentRepository {

    Optional<Content> findById(Long id);

    Content save(Content content);

    Optional<Content> findByImdbId(String imdbId);

    boolean existsByImdbId(String imdbId);
}
