package br.com.alexandreluchetti.cinealert.dataprovider.entity;

import br.com.alexandreluchetti.cinealert.core.model.content.Content;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "contents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("imdb_id")
    private String imdbId;

    @Field("title")
    private String title;

    @Field("type")
    private ContentType type;

    @Field("poster_url")
    private String posterUrl;

    @Field("year")
    private Integer year;

    @Field("rating")
    private BigDecimal rating;

    @Field("genre")
    private String genre;

    @Field("synopsis")
    private String synopsis;

    @Field("trailer_url")
    private String trailerUrl;

    @Field("runtime_minutes")
    private Integer runtimeMinutes;

    @Builder.Default
    @Field("cached_at")
    private LocalDateTime cachedAt = LocalDateTime.now();

    public static ContentEntity fromModel(Content content) {
        return new ContentEntity(
                content.getId(),
                content.getImdbId(),
                content.getTitle(),
                content.getType(),
                content.getPosterUrl(),
                content.getYear(),
                content.getRating(),
                content.getGenre(),
                content.getSynopsis(),
                content.getTrailerUrl(),
                content.getRuntimeMinutes(),
                content.getCachedAt()
        );
    }

    public Content toModel() {
        return new Content(
                id,
                imdbId,
                title,
                type,
                posterUrl,
                year,
                rating,
                genre,
                synopsis,
                trailerUrl,
                runtimeMinutes,
                cachedAt
        );
    }
}
