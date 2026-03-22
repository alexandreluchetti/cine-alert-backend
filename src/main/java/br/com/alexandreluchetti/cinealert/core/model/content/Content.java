package br.com.alexandreluchetti.cinealert.core.model.content;

import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Content {

    private String id;
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
    private LocalDateTime cachedAt;

    public Content(String id, String imdbId, String title, ContentType type, String posterUrl, Integer year, BigDecimal rating, String genre, String synopsis, String trailerUrl, Integer runtimeMinutes, LocalDateTime cachedAt) {
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
        this.cachedAt = cachedAt;
    }

    public Content(String id, String imdbId, String title, ContentType type, String posterUrl, Integer year, BigDecimal rating, String genre, String synopsis, String trailerUrl, Integer runtimeMinutes) {
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
        this.cachedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public Integer getRuntimeMinutes() {
        return runtimeMinutes;
    }

    public void setRuntimeMinutes(Integer runtimeMinutes) {
        this.runtimeMinutes = runtimeMinutes;
    }

    public LocalDateTime getCachedAt() {
        return cachedAt;
    }

    public void setCachedAt(LocalDateTime cachedAt) {
        this.cachedAt = cachedAt;
    }
}
