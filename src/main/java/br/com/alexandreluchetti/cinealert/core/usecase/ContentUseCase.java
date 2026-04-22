package br.com.alexandreluchetti.cinealert.core.usecase;

import br.com.alexandreluchetti.cinealert.core.model.content.Content;
import br.com.alexandreluchetti.cinealert.core.model.content.ContentResponse;

import java.util.List;

public interface ContentUseCase {

    List<ContentResponse> search(String query, String type, String genre, Integer year, Double minRating);

    ContentResponse getDetail(String imdbId);

    Content getOrCacheContent(String contentId);

    List<ContentResponse> getTrending();

    List<String> getGenres();

    List<ContentResponse> getMostPopularMovies();
}
