package br.com.alexandreluchetti.cinealert.core.model.content;

import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.model.enums.GenreEnum;

import java.math.BigDecimal;

public class ContentResponse {

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

    public ContentResponse(
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
        this.id = id;
        this.imdbId = imdbId;
        this.title = title;
        this.type = type;
        this.posterUrl = posterUrl;
        this.year = year;
        this.rating = rating;
        this.genre = genre;
        this.synopsis = synopsis;
        this.trailerUrl = trailerUrl;
        this.runtimeMinutes = runtimeMinutes;
    }

    public Long getId() {
        return id;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getTitle() {
        return title;
    }

    public ContentType getType() {
        return type;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public Integer getYear() {
        return year;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public String getGenre() {
        return genre;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public Integer getRuntimeMinutes() {
        return runtimeMinutes;
    }
}
