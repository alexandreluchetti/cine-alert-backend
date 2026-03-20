package br.com.alexandreluchetti.cinealert.core.dto.content;

import br.com.alexandreluchetti.cinealert.model.enums.ContentType;

import java.math.BigDecimal;

public record ContentResponse(
    Long id,
    String imdbId,
    String title,
    ContentType type,
    String posterUrl,
    Integer year,
    BigDecimal rating,
    String genre,
    String synopsis,
    String trailerUrl,
    Integer runtimeMinutes
) {}
