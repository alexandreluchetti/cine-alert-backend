package br.com.alexandreluchetti.cinealert.core.model;

import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Content {

    private Long id;

    private String imdbId;

    private String title;

    private ContentType type;

    private String posterUrl;

    private Integer year;

    private BigDecimal rating;

    private String genre;

    private String synopsis;

    private String trailerUrl;

    private Integer runtimeMinutes;

    @Builder.Default
    private LocalDateTime cachedAt = LocalDateTime.now();
}
