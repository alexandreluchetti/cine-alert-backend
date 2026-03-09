package com.cinealert.controller;

import com.cinealert.dto.content.ContentResponse;
import com.cinealert.service.ContentService;
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

    private final ContentService contentService;

    @GetMapping("/search")
    @Operation(summary = "Search movies and series")
    public ResponseEntity<List<ContentResponse>> search(
            @RequestParam String q,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Double rating) {
        return ResponseEntity.ok(contentService.search(q, type, genre, year, rating));
    }

    @GetMapping("/{imdbId}")
    @Operation(summary = "Get content detail by IMDB ID")
    public ResponseEntity<ContentResponse> getDetail(@PathVariable String imdbId) {
        return ResponseEntity.ok(contentService.getDetail(imdbId));
    }

    @GetMapping("/trending")
    @Operation(summary = "Get trending movies")
    public ResponseEntity<List<ContentResponse>> getTrending() {
        return ResponseEntity.ok(contentService.getTrending());
    }

    @GetMapping("/genres")
    @Operation(summary = "Get available genres")
    public ResponseEntity<List<String>> getGenres() {
        return ResponseEntity.ok(contentService.getGenres());
    }
}
