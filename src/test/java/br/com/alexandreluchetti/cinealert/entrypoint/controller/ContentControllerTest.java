package br.com.alexandreluchetti.cinealert.entrypoint.controller;

import br.com.alexandreluchetti.cinealert.core.model.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.usecase.ContentUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.alexandreluchetti.cinealert.configuration.shared.JwtUtilImpl;
import br.com.alexandreluchetti.cinealert.core.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ContentController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContentUseCase contentUseCase;

    @MockBean
    private JwtUtilImpl jwtUtilImpl;

    @MockBean
    private UserRepository userRepository;

    @Test
    void search_returns200() throws Exception {
        ContentResponse cr = buildContentResponse("tt123", "Inception");
        when(contentUseCase.search(eq("Inception"), any(), any(), any(), any())).thenReturn(List.of(cr));

        mockMvc.perform(get("/api/content/search").param("q", "Inception"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imdbId").value("tt123"));
    }

    @Test
    void getDetail_returns200() throws Exception {
        ContentResponse cr = buildContentResponse("tt123", "Inception");
        when(contentUseCase.getDetail("tt123")).thenReturn(cr);

        mockMvc.perform(get("/api/content/tt123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imdbId").value("tt123"));
    }

    @Test
    void getTrending_returns200() throws Exception {
        ContentResponse cr = buildContentResponse("tt123", "Trending");
        when(contentUseCase.getTrending()).thenReturn(List.of(cr));

        mockMvc.perform(get("/api/content/trending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imdbId").value("tt123"));
    }

    @Test
    void getMostPopularMovies_returns200() throws Exception {
        ContentResponse cr = buildContentResponse("tt123", "Popular");
        when(contentUseCase.getMostPopularMovies()).thenReturn(List.of(cr));

        mockMvc.perform(get("/api/content/trending/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imdbId").value("tt123"));
    }

    @Test
    void getGenres_returns200() throws Exception {
        when(contentUseCase.getGenres()).thenReturn(List.of("Action", "Drama"));

        mockMvc.perform(get("/api/content/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Action"))
                .andExpect(jsonPath("$[1]").value("Drama"));
    }

    private ContentResponse buildContentResponse(String imdbId, String title) {
        return new ContentResponse("c-1", imdbId, title, ContentType.MOVIE, "url",
                2020, new BigDecimal("8.0"), Collections.emptyList(), "Syn", "Trailer", 120);
    }
}
