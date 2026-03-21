package br.com.alexandreluchetti.cinealert.dataprovider.repository;

import br.com.alexandreluchetti.cinealert.core.model.Content;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.repository.ContentRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class ContentRepositoryImpl implements ContentRepository {

    private final JdbcClient jdbcClient;

    public ContentRepositoryImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private Content mapRow(ResultSet rs, int rowNum) throws SQLException {
        Content content = new Content();
        content.setId(rs.getLong("id"));
        content.setImdbId(rs.getString("imdb_id"));
        content.setTitle(rs.getString("title"));
        if (rs.getString("type") != null) {
            content.setType(ContentType.valueOf(rs.getString("type")));
        }
        content.setPosterUrl(rs.getString("poster_url"));
        
        int year = rs.getInt("year");
        if (!rs.wasNull()) {
            content.setYear(year);
        }
        
        if (rs.getBigDecimal("rating") != null) {
            content.setRating(rs.getBigDecimal("rating"));
        }
        content.setGenre(rs.getString("genre"));
        content.setSynopsis(rs.getString("synopsis"));
        content.setTrailerUrl(rs.getString("trailer_url"));
        
        int runtime = rs.getInt("runtime_minutes");
        if (!rs.wasNull()) {
            content.setRuntimeMinutes(runtime);
        }
        
        if (rs.getTimestamp("cached_at") != null) {
            content.setCachedAt(rs.getTimestamp("cached_at").toLocalDateTime());
        }
        return content;
    }

    @Override
    public Optional<Content> findById(Long id) {
        return jdbcClient.sql("SELECT * FROM contents WHERE id = ?")
                .param(id)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public Content save(Content content) {
        if (content.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcClient.sql("INSERT INTO contents (imdb_id, title, type, poster_url, year, rating, genre, synopsis, trailer_url, runtime_minutes, cached_at) " +
                            "VALUES (:imdbId, :title, :type, :posterUrl, :year, :rating, :genre, :synopsis, :trailerUrl, :runtimeMinutes, :cachedAt)")
                    .param("imdbId", content.getImdbId())
                    .param("title", content.getTitle())
                    .param("type", content.getType() != null ? content.getType().name() : null)
                    .param("posterUrl", content.getPosterUrl())
                    .param("year", content.getYear())
                    .param("rating", content.getRating())
                    .param("genre", content.getGenre())
                    .param("synopsis", content.getSynopsis())
                    .param("trailerUrl", content.getTrailerUrl())
                    .param("runtimeMinutes", content.getRuntimeMinutes())
                    .param("cachedAt", content.getCachedAt())
                    .update(keyHolder);
            if (keyHolder.getKey() != null) {
                content.setId(keyHolder.getKey().longValue());
            }
        } else {
            jdbcClient.sql("UPDATE contents SET imdb_id = :imdbId, title = :title, type = :type, " +
                            "poster_url = :posterUrl, year = :year, rating = :rating, genre = :genre, " +
                            "synopsis = :synopsis, trailer_url = :trailerUrl, runtime_minutes = :runtimeMinutes, " +
                            "cached_at = :cachedAt WHERE id = :id")
                    .param("imdbId", content.getImdbId())
                    .param("title", content.getTitle())
                    .param("type", content.getType() != null ? content.getType().name() : null)
                    .param("posterUrl", content.getPosterUrl())
                    .param("year", content.getYear())
                    .param("rating", content.getRating())
                    .param("genre", content.getGenre())
                    .param("synopsis", content.getSynopsis())
                    .param("trailerUrl", content.getTrailerUrl())
                    .param("runtimeMinutes", content.getRuntimeMinutes())
                    .param("cachedAt", content.getCachedAt())
                    .param("id", content.getId())
                    .update();
        }
        return content;
    }

    @Override
    public Optional<Content> findByImdbId(String imdbId) {
        return jdbcClient.sql("SELECT * FROM contents WHERE imdb_id = ?")
                .param(imdbId)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public boolean existsByImdbId(String imdbId) {
        return jdbcClient.sql("SELECT COUNT(1) FROM contents WHERE imdb_id = ?")
                .param(imdbId)
                .query(Integer.class)
                .single() > 0;
    }
}
