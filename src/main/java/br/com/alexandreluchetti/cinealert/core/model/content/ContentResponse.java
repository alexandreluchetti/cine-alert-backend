package br.com.alexandreluchetti.cinealert.core.model.content;

import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.model.enums.GenreEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContentResponse {

    private String id;
    private String imdbId;
    private String title;
    private ContentType type;
    private String posterUrl;
    private Integer year;
    private BigDecimal rating;
    private List<GenreEnum> genres;
    private String synopsis;
    private String trailerUrl;
    private Integer runtimeMinutes;

    public ContentResponse(
            String id,
            String imdbId,
            String title,
            ContentType type,
            String posterUrl,
            Integer year,
            BigDecimal rating,
            List<GenreEnum> genres,
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
        this.genres = genres;
        this.synopsis = synopsis;
        this.trailerUrl = trailerUrl;
        this.runtimeMinutes = runtimeMinutes;
    }

    public static List<GenreEnum> fromString(String genres) {
        if (genres == null || genres.isEmpty()) {
            return Collections.emptyList();
        }

        List<GenreEnum> list = new ArrayList<>();
        for (String s : genres.split(",")) {
            GenreEnum genreEnum = GenreEnum.valueOf(s.trim());
            if (genreEnum != null) {
                list.add(genreEnum);
            }
        }
        return list;
    }

    public String getId() {
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

    public List<GenreEnum> getGenres() {
        return genres;
    }

    public String getGenreString() {
        return genres.stream()
                .map(GenreEnum::getValue)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
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
