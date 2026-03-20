package br.com.alexandreluchetti.cinealert.service;

import br.com.alexandreluchetti.cinealert.dto.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.exception.AppException;
import br.com.alexandreluchetti.cinealert.integration.ImdbApiClient;
import br.com.alexandreluchetti.cinealert.model.Content;
import br.com.alexandreluchetti.cinealert.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ImdbApiClient imdbApiClient;
    private final ContentRepository contentRepository;

    // Cache expires after 24 hours
    private static final int CACHE_HOURS = 24;

    public List<ContentResponse> search(String query, String type, String genre, Integer year, Double minRating) {
        return imdbApiClient.search(query, type, genre, year, minRating);
    }

    @Transactional
    public ContentResponse getDetail(String imdbId) {
        // Check cache
        Optional<Content> cached = contentRepository.findByImdbId(imdbId);
        if (cached.isPresent() && cached.get().getCachedAt().isAfter(LocalDateTime.now().minusHours(CACHE_HOURS))) {
            return toResponse(cached.get());
        }

        // Fetch from IMDB
        ContentResponse response = imdbApiClient.getDetail(imdbId)
                .orElseThrow(() -> AppException.notFound("Content not found: " + imdbId));

        // Cache it
        cacheContent(imdbId, response, cached.orElse(null));

        return response;
    }

    @Transactional
    public Content getOrCacheContent(Long contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> AppException.notFound("Content not found with id: " + contentId));
    }

    public List<ContentResponse> getTrending() {
        return imdbApiClient.getTrending();
    }

    public List<String> getGenres() {
        return imdbApiClient.getGenres();
    }

    private void cacheContent(String imdbId, ContentResponse response, Content existing) {
        Content content = existing != null ? existing : Content.builder().imdbId(imdbId).build();
        content.setTitle(response.title());
        content.setType(response.type());
        content.setPosterUrl(response.posterUrl());
        content.setYear(response.year());
        content.setRating(response.rating());
        content.setGenre(response.genre());
        content.setSynopsis(response.synopsis());
        content.setTrailerUrl(response.trailerUrl());
        content.setRuntimeMinutes(response.runtimeMinutes());
        content.setCachedAt(LocalDateTime.now());
        contentRepository.save(content);
    }

    private ContentResponse toResponse(Content c) {
        return new ContentResponse(
                c.getId(), c.getImdbId(), c.getTitle(), c.getType(),
                c.getPosterUrl(), c.getYear(), c.getRating(),
                c.getGenre(), c.getSynopsis(), c.getTrailerUrl(), c.getRuntimeMinutes());
    }
}
