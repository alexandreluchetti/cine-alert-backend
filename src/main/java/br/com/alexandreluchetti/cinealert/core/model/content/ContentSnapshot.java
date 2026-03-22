package br.com.alexandreluchetti.cinealert.core.model.content;

import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;

public class ContentSnapshot {

    private String imdbId;
    private String title;
    private ContentType type;
    private String posterUrl;
    private Integer year;

    public ContentSnapshot(String imdbId, String title, ContentType type, String posterUrl, Integer year) {
        this.imdbId = imdbId;
        this.title = title;
        this.type = type;
        this.posterUrl = posterUrl;
        this.year = year;
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
}
