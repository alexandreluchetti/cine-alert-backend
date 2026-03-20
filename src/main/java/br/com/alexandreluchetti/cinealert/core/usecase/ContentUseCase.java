package br.com.alexandreluchetti.cinealert.core.usecase;

import br.com.alexandreluchetti.cinealert.dto.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.model.Content;

import java.util.List;

public interface ContentUseCase {

    List<ContentResponse> search(String query, String type, String genre, Integer year, Double minRating);

    ContentResponse getDetail(String imdbId);

    Content getOrCacheContent(Long contentId);

    List<ContentResponse> getTrending();

    List<String> getGenres();
}
