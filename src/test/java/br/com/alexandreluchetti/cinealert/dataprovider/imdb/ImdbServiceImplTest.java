package br.com.alexandreluchetti.cinealert.dataprovider.imdb;

import br.com.alexandreluchetti.cinealert.core.model.content.ContentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ImdbServiceImplTest {

    @InjectMocks
    private ImdbServiceImpl imdbService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imdbService, "apiKey", "test-key");
        ReflectionTestUtils.setField(imdbService, "baseUrl", "https://invalid-url.com");
        ReflectionTestUtils.setField(imdbService, "apiHost", "test-host");
    }

    @Test
    void search_onException_returnsEmptyList() {
        List<ContentResponse> results = imdbService.search("Inception", null, null, null, null);
        assertThat(results).isEmpty();
    }

    @Test
    void getDetail_onException_returnsEmptyOptional() {
        Optional<ContentResponse> result = imdbService.getDetail("tt123");
        assertThat(result).isEmpty();
    }

    @Test
    void getTrending_onException_returnsEmptyList() {
        List<ContentResponse> results = imdbService.getTrending();
        assertThat(results).isEmpty();
    }

    @Test
    void getMostPopularMovies_onException_returnsEmptyList() {
        List<ContentResponse> results = imdbService.getMostPopularMovies();
        assertThat(results).isEmpty();
    }

    @Test
    void getGenres_returnsListOfGenres() {
        List<String> genres = imdbService.getGenres();
        assertThat(genres).isNotEmpty();
        assertThat(genres).contains("Action", "Drama");
    }
}
