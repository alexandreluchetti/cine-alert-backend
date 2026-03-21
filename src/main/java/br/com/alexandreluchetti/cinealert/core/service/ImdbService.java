package br.com.alexandreluchetti.cinealert.core.service;

import br.com.alexandreluchetti.cinealert.core.model.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.entrypoint.dto.content.ContentResponseDto;

import java.util.List;
import java.util.Optional;

public interface ImdbService {

    List<ContentResponse> search(String query, String type, String genre, Integer year, Double minRating);

    Optional<ContentResponse> getDetail(String imdbId);

    List<ContentResponseDto> getTrending();

    List<String> getGenres();
}
