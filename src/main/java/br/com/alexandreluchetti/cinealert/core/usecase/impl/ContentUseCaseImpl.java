package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.dataprovider.entity.ContentEntity;
import br.com.alexandreluchetti.cinealert.core.model.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.core.repository.ContentRepository;
import br.com.alexandreluchetti.cinealert.core.service.ImdbService;
import br.com.alexandreluchetti.cinealert.core.usecase.ContentUseCase;
import br.com.alexandreluchetti.cinealert.configuration.exception.AppException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ContentUseCaseImpl implements ContentUseCase {

    private final ImdbService imdbService;
    private final ContentRepository contentRepository;

    public ContentUseCaseImpl(ImdbService imdbService, ContentRepository contentRepository) {
        this.imdbService = imdbService;
        this.contentRepository = contentRepository;
    }

    private static final int CACHE_HOURS = 24;

    @Override
    public List<ContentResponse> search(String query, String type, String genre, Integer year, Double minRating) {
        return imdbService.search(query, type, genre, year, minRating);
    }

    @Override
    public ContentResponse getDetail(String imdbId) {
        Optional<ContentEntity> cached = contentRepository.findByImdbId(imdbId);
        if (cached.isPresent() && cached.get().getCachedAt().isAfter(LocalDateTime.now().minusHours(CACHE_HOURS))) {
            return toResponse(cached.get());
        }

        ContentResponse response = imdbService.getDetail(imdbId)
                .orElseThrow(() -> AppException.notFound("Content not found: " + imdbId));

        cacheContent(imdbId, response, cached.orElse(null));

        return response;
    }

    @Override
    public ContentEntity getOrCacheContent(String contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> AppException.notFound("Content not found with id: " + contentId));
    }

    @Override
    public List<ContentResponse> getTrending() {
        List<ContentResponse> basicTrending = imdbService.getTrending();
        List<ContentResponse> detailedTrending = new java.util.ArrayList<>();
        
        for (ContentResponse basic : basicTrending) {
            try {
                ContentResponse detail = this.getDetail(basic.getImdbId());
                detailedTrending.add(detail);
            } catch (Exception e) {
                log.warn("Failed to fetch detailed info for trending ID {}: {}", basic.getImdbId(), e.getMessage());
                detailedTrending.add(basic);
            }
        }
        
        return detailedTrending;
    }

    @Override
    public List<String> getGenres() {
        return imdbService.getGenres();
    }

    private void cacheContent(String imdbId, ContentResponse response, ContentEntity existing) {
        ContentEntity contentEntity = existing != null ? existing : ContentEntity.builder().imdbId(imdbId).build();
        contentEntity.setTitle(response.getTitle());
        contentEntity.setType(response.getType());
        contentEntity.setPosterUrl(response.getPosterUrl());
        contentEntity.setYear(response.getYear());
        contentEntity.setRating(response.getRating());
        contentEntity.setGenre(response.getGenreString());
        contentEntity.setSynopsis(response.getSynopsis());
        contentEntity.setTrailerUrl(response.getTrailerUrl());
        contentEntity.setRuntimeMinutes(response.getRuntimeMinutes());
        contentEntity.setCachedAt(LocalDateTime.now());
        contentRepository.save(contentEntity);
    }

    private ContentResponse toResponse(ContentEntity c) {
        return new ContentResponse(
                c.getId(), c.getImdbId(), c.getTitle(), c.getType(),
                c.getPosterUrl(), c.getYear(), c.getRating(),
                ContentResponse.fromString(c.getGenre()),
                c.getSynopsis(), c.getTrailerUrl(), c.getRuntimeMinutes());
    }
}
