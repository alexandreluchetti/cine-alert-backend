package br.com.alexandreluchetti.cinealert.repository;

import br.com.alexandreluchetti.cinealert.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    Optional<Content> findByImdbId(String imdbId);
    boolean existsByImdbId(String imdbId);
}
