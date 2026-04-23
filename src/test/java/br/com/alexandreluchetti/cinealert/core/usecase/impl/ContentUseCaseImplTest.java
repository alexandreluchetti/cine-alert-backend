package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.exception.AppException;
import br.com.alexandreluchetti.cinealert.core.model.content.Content;
import br.com.alexandreluchetti.cinealert.core.model.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.repository.ContentRepository;
import br.com.alexandreluchetti.cinealert.core.service.ImdbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentUseCaseImplTest {

    @Mock
    private ImdbService imdbService;

    @Mock
    private ContentRepository contentRepository;

    private ContentUseCaseImpl contentUseCase;

    @BeforeEach
    void setUp() {
        contentUseCase = new ContentUseCaseImpl(imdbService, contentRepository);
    }

    // ─────────────────────────── search ───────────────────────────

    @Test
    void search_delegatesToImdbService() {
        ContentResponse cr = buildContentResponse("tt001", "Inception");
        when(imdbService.search("Inception", "movie", null, null, null)).thenReturn(List.of(cr));

        List<ContentResponse> result = contentUseCase.search("Inception", "movie", null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Inception");
        verify(imdbService).search("Inception", "movie", null, null, null);
    }

    // ─────────────────────────── getDetail ───────────────────────────

    @Test
    void getDetail_freshCacheHit_returnsCachedContent() {
        Content cached = buildContent("tt001");
        cached.setCachedAt(LocalDateTime.now()); // freshly cached

        when(contentRepository.findByImdbId("tt001")).thenReturn(Optional.of(cached));

        ContentResponse result = contentUseCase.getDetail("tt001");

        assertThat(result.getImdbId()).isEqualTo("tt001");
        verify(imdbService, never()).getDetail(any());
    }

    @Test
    void getDetail_staleCacheHit_fetchesFromImdbAndUpdates() {
        Content stale = buildContent("tt001");
        stale.setCachedAt(LocalDateTime.now().minusHours(25)); // stale

        ContentResponse imdbResp = buildContentResponse("tt001", "Inception");

        when(contentRepository.findByImdbId("tt001")).thenReturn(Optional.of(stale));
        when(imdbService.getDetail("tt001")).thenReturn(Optional.of(imdbResp));
        when(contentRepository.save(any(Content.class))).thenReturn(stale);

        ContentResponse result = contentUseCase.getDetail("tt001");

        assertThat(result.getTitle()).isEqualTo("Inception");
        verify(imdbService).getDetail("tt001");
        verify(contentRepository).save(any(Content.class));
    }

    @Test
    void getDetail_noCacheAndImdbReturnsResult_cachesAndReturns() {
        ContentResponse imdbResp = buildContentResponse("tt002", "Interstellar");

        when(contentRepository.findByImdbId("tt002")).thenReturn(Optional.empty());
        when(imdbService.getDetail("tt002")).thenReturn(Optional.of(imdbResp));
        when(contentRepository.save(any(Content.class))).thenAnswer(inv -> inv.getArgument(0));

        ContentResponse result = contentUseCase.getDetail("tt002");

        assertThat(result.getTitle()).isEqualTo("Interstellar");
        verify(contentRepository).save(any(Content.class));
    }

    @Test
    void getDetail_notFoundInImdb_throwsNotFound() {
        when(contentRepository.findByImdbId("ttXXX")).thenReturn(Optional.empty());
        when(imdbService.getDetail("ttXXX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contentUseCase.getDetail("ttXXX"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Content not found: ttXXX");
    }

    // ─────────────────────────── getOrCacheContent ───────────────────────────

    @Test
    void getOrCacheContent_found_returnsContent() {
        Content content = buildContent("tt001");
        when(contentRepository.findById("id-1")).thenReturn(Optional.of(content));

        Content result = contentUseCase.getOrCacheContent("id-1");

        assertThat(result.getImdbId()).isEqualTo("tt001");
    }

    @Test
    void getOrCacheContent_notFound_throwsNotFound() {
        when(contentRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contentUseCase.getOrCacheContent("bad-id"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Content not found with id: bad-id");
    }

    // ─────────────────────────── getTrending ───────────────────────────

    @Test
    void getTrending_returnsDetailedList() {
        ContentResponse basic = buildContentResponse("tt003", "Trending Movie");
        basic = new ContentResponse("id-3", "tt003", "Trending Movie",
                ContentType.MOVIE, null, 2023, null,
                Collections.emptyList(), null, null, null);

        when(imdbService.getTrending()).thenReturn(List.of(basic));

        // getDetail for "tt003" — no cache, imdb returns result
        ContentResponse detail = buildContentResponse("tt003", "Trending Movie");
        when(contentRepository.findByImdbId("tt003")).thenReturn(Optional.empty());
        when(imdbService.getDetail("tt003")).thenReturn(Optional.of(detail));
        when(contentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        List<ContentResponse> result = contentUseCase.getTrending();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Trending Movie");
    }

    @Test
    void getTrending_imdbDetailFails_returnsBasicContent() {
        ContentResponse basic = buildContentResponse("tt004", "Fallback Movie");

        when(imdbService.getTrending()).thenReturn(List.of(basic));
        when(contentRepository.findByImdbId("tt004")).thenReturn(Optional.empty());
        when(imdbService.getDetail("tt004")).thenThrow(new RuntimeException("IMDB down"));

        List<ContentResponse> result = contentUseCase.getTrending();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Fallback Movie");
    }

    // ─────────────────────────── getMostPopularMovies ───────────────────────────

    @Test
    void getMostPopularMovies_returnsDetailedList() {
        ContentResponse basic = buildContentResponse("tt005", "Popular Movie");

        when(imdbService.getMostPopularMovies()).thenReturn(List.of(basic));
        when(contentRepository.findByImdbId("tt005")).thenReturn(Optional.empty());
        when(imdbService.getDetail("tt005")).thenReturn(Optional.of(basic));
        when(contentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        List<ContentResponse> result = contentUseCase.getMostPopularMovies();

        assertThat(result).hasSize(1);
    }

    // ─────────────────────────── getGenres ───────────────────────────

    @Test
    void getGenres_delegatesToImdbService() {
        when(imdbService.getGenres()).thenReturn(List.of("Action", "Drama"));

        List<String> genres = contentUseCase.getGenres();

        assertThat(genres).containsExactly("Action", "Drama");
    }

    // ─────────────────────────── helpers ───────────────────────────

    private Content buildContent(String imdbId) {
        Content c = new Content(imdbId);
        c.setId("id-" + imdbId);
        c.setTitle("Title for " + imdbId);
        c.setType(ContentType.MOVIE);
        c.setPosterUrl("https://poster.url");
        c.setYear(2020);
        c.setRating(new BigDecimal("7.5"));
        c.setCachedAt(LocalDateTime.now());
        return c;
    }

    private ContentResponse buildContentResponse(String imdbId, String title) {
        return new ContentResponse("id-" + imdbId, imdbId, title,
                ContentType.MOVIE, "https://poster.url", 2020,
                new BigDecimal("7.5"), Collections.emptyList(),
                "Synopsis", null, 120);
    }
}
