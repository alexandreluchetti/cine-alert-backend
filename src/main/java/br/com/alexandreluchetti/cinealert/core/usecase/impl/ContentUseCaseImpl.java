package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.exception.AppException;
import br.com.alexandreluchetti.cinealert.core.model.content.Content;
import br.com.alexandreluchetti.cinealert.core.model.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.core.repository.ContentRepository;
import br.com.alexandreluchetti.cinealert.core.service.ImdbService;
import br.com.alexandreluchetti.cinealert.core.usecase.ContentUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContentUseCaseImpl implements ContentUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentUseCaseImpl.class);

    private final ImdbService imdbService;
    private final ContentRepository contentRepository;

    public ContentUseCaseImpl(ImdbService imdbService, ContentRepository contentRepository) {
        this.imdbService = imdbService;
        this.contentRepository = contentRepository;
    }

    private static final int CACHE_HOURS = 24;

    @Override
    public List<ContentResponse> search(String query, String type, String genre, Integer year, Double minRating) {
        LOGGER.info("Searching for contents with query {}, {}, {}, {}, {}", query, type, genre, year, minRating);
        List<ContentResponse> contents = imdbService.search(query, type, genre, year, minRating);
        LOGGER.info("Found {} contents", contents.size());
        return contents;
    }

    @Override
    public ContentResponse getDetail(String imdbId) {
        LOGGER.info("Getting details of imdb {}", imdbId);

        Optional<Content> cached = contentRepository.findByImdbId(imdbId);
        if (cached.isPresent() && cached.get().getCachedAt().isAfter(LocalDateTime.now().minusHours(CACHE_HOURS))) {
            LOGGER.debug("Returning cached content for imdb {}", imdbId);
            return toResponse(cached.get());
        }

        ContentResponse response = imdbService.getDetail(imdbId)
                .orElseThrow(() -> AppException.notFound("Content not found: " + imdbId));

        Content cachedContent = cacheContent(imdbId, response, cached.orElse(null));
        LOGGER.info("Returning cached content {}", cachedContent.getTitle());
        return toResponse(cachedContent);
    }

    @Override
    public Content getOrCacheContent(String contentId) {
        LOGGER.info("Getting cached content {}", contentId);
        return contentRepository.findById(contentId)
                .orElseThrow(() -> AppException.notFound("Content not found with id: " + contentId));
    }

    @Override
    public List<ContentResponse> getTrending() {
        LOGGER.info("Getting trending");

        List<ContentResponse> basicTrending = imdbService.getTrending();
        List<ContentResponse> detailedTrending = getDetailedTranding(basicTrending);

        LOGGER.info("Found {} trending", detailedTrending.size());
        return detailedTrending;
    }

    public List<ContentResponse> getMostPopularMovies() {
        LOGGER.info("Getting most popular movies");

        List<ContentResponse> basicPopularMovies = imdbService.getMostPopularMovies();
        List<ContentResponse> detailedTrending = getDetailedTranding(basicPopularMovies);

        LOGGER.info("Found {} trending", detailedTrending.size());
        return detailedTrending;
    }

    private List<ContentResponse> getDetailedTranding(List<ContentResponse> basicTrending) {
        List<ContentResponse> detailedTrending = new ArrayList<>();
        for (ContentResponse basic : basicTrending) {
            try {
                ContentResponse detail = this.getDetail(basic.getImdbId());
                detailedTrending.add(detail);
            } catch (Exception e) {
                LOGGER.warn("Failed to fetch detailed info for trending ID {}: {}", basic.getImdbId(), e.getMessage());
                detailedTrending.add(basic);
            }
        }
        return detailedTrending;
    }

    @Override
    public List<String> getGenres() {
        return imdbService.getGenres();
    }

    private Content cacheContent(String imdbId, ContentResponse response, Content existing) {
        Content content = existing != null ? existing : new Content(imdbId);
        content.setTitle(response.getTitle());
        content.setType(response.getType());
        content.setPosterUrl(response.getPosterUrl());
        content.setYear(response.getYear());
        content.setRating(response.getRating());
        content.setGenre(response.getGenreString());
        content.setSynopsis(response.getSynopsis());
        content.setTrailerUrl(response.getTrailerUrl());
        content.setRuntimeMinutes(response.getRuntimeMinutes());
        content.setCachedAt(LocalDateTime.now());
        LOGGER.info("Cached content for imdb {}", imdbId);
        return contentRepository.save(content);
    }

    private ContentResponse toResponse(Content c) {
        return new ContentResponse(
                c.getId(), c.getImdbId(), c.getTitle(), c.getType(),
                c.getPosterUrl(), c.getYear(), c.getRating(),
                ContentResponse.fromString(c.getGenre()),
                c.getSynopsis(), c.getTrailerUrl(), c.getRuntimeMinutes());
    }
}
