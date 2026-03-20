package br.com.alexandreluchetti.cinealert.entrypoint.controller;

import br.com.alexandreluchetti.cinealert.core.usecase.ContentUseCase;
import br.com.alexandreluchetti.cinealert.entrypoint.dto.content.ContentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
@Tag(name = "Content", description = "Search and retrieve movies/series from IMDB")
public class ContentController {

    private final ContentUseCase contentUseCase;

    @GetMapping("/search")
    @Operation(summary = "Search movies and series")
    public ResponseEntity<List<ContentResponse>> search(
            @RequestParam String q,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Double rating) {
        return ResponseEntity.ok(contentUseCase.search(q, type, genre, year, rating));
    }

    @GetMapping("/{imdbId}")
    @Operation(summary = "Get content detail by IMDB ID")
    public ResponseEntity<ContentResponse> getDetail(@PathVariable String imdbId) {
        return ResponseEntity.ok(contentUseCase.getDetail(imdbId));
    }

    @GetMapping("/trending")
    @Operation(summary = "Get trending movies")
    public ResponseEntity<List<ContentResponse>> getTrending() {
        return ResponseEntity.ok(contentUseCase.getTrending());
    }

    @GetMapping("/genres")
    @Operation(summary = "Get available genres")
    public ResponseEntity<List<String>> getGenres() {
        return ResponseEntity.ok(contentUseCase.getGenres());
    }
}
