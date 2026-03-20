package br.com.alexandreluchetti.cinealert.core.service;

import br.com.alexandreluchetti.cinealert.core.dto.content.ContentResponse;

import java.util.List;
import java.util.Optional;

public interface ImdbService {

    List<ContentResponse> search(String query, String type, String genre, Integer year, Double minRating);

    Optional<ContentResponse> getDetail(String imdbId);

    List<ContentResponse> getTrending();

    List<String> getGenres();
}
