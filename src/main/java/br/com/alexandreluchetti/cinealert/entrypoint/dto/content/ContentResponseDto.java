package br.com.alexandreluchetti.cinealert.entrypoint.dto.content;

import br.com.alexandreluchetti.cinealert.core.model.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;

import java.math.BigDecimal;

public record ContentResponseDto (
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
) {

    public static ContentResponseDto fromModel(ContentResponse contentResponse) {
        return new ContentResponseDto(
                contentResponse.getId(),
                contentResponse.getImdbId(),
                contentResponse.getTitle(),
                contentResponse.getType(),
                contentResponse.getPosterUrl(),
                contentResponse.getYear(),
                contentResponse.getRating(),
                contentResponse.getGenreString(),
                contentResponse.getSynopsis(),
                contentResponse.getTrailerUrl(),
                contentResponse.getRuntimeMinutes()
        );
    }
}
