package br.com.alexandreluchetti.cinealert.core.repository;


import br.com.alexandreluchetti.cinealert.core.model.content.Content;

import java.util.Optional;

public interface ContentRepository {

    Optional<Content> findByImdbId(String imdbId);

    boolean existsByImdbId(String imdbId);

    Optional<Content> findById(String contentId);

    Content save(Content contentEntity);
}
