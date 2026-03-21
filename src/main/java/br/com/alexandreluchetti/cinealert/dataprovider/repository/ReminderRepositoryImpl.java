package br.com.alexandreluchetti.cinealert.dataprovider.repository;

import br.com.alexandreluchetti.cinealert.core.model.Content;
import br.com.alexandreluchetti.cinealert.core.model.Reminder;
import br.com.alexandreluchetti.cinealert.core.model.User;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.core.repository.ReminderRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ReminderRepositoryImpl implements ReminderRepository {

    private final JdbcClient jdbcClient;

    public ReminderRepositoryImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private static final String SELECT_JOIN = "SELECT r.*, " +
        "u.id as u_id, u.name as u_name, u.email as u_email, u.password as u_password, " +
        "u.avatar_url as u_avatar_url, u.fcm_token as u_fcm_token, u.active as u_active, u.created_at as u_created_at, u.updated_at as u_updated_at, " +
        "c.id as c_id, c.imdb_id as c_imdb_id, c.title as c_title, c.type as c_type, " +
        "c.poster_url as c_poster_url, c.year as c_year, c.rating as c_rating, c.genre as c_genre, " +
        "c.synopsis as c_synopsis, c.trailer_url as c_trailer_url, c.runtime_minutes as c_runtime_minutes, c.cached_at as c_cached_at " +
        "FROM reminders r " +
        "JOIN users u ON r.user_id = u.id " +
        "JOIN contents c ON r.content_id = c.id ";

    private Reminder mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Map User
        User user = new User();
        user.setId(rs.getLong("u_id"));
        user.setName(rs.getString("u_name"));
        user.setEmail(rs.getString("u_email"));
        user.setPassword(rs.getString("u_password"));
        user.setAvatarUrl(rs.getString("u_avatar_url"));
        user.setFcmToken(rs.getString("u_fcm_token"));
        user.setActive(rs.getBoolean("u_active"));
        if (rs.getTimestamp("u_created_at") != null) user.setCreatedAt(rs.getTimestamp("u_created_at").toLocalDateTime());
        if (rs.getTimestamp("u_updated_at") != null) user.setUpdatedAt(rs.getTimestamp("u_updated_at").toLocalDateTime());

        // Map Content
        Content content = new Content();
        content.setId(rs.getLong("c_id"));
        content.setImdbId(rs.getString("c_imdb_id"));
        content.setTitle(rs.getString("c_title"));
        if (rs.getString("c_type") != null) content.setType(ContentType.valueOf(rs.getString("c_type")));
        content.setPosterUrl(rs.getString("c_poster_url"));
        int year = rs.getInt("c_year");
        if (!rs.wasNull()) content.setYear(year);
        if (rs.getBigDecimal("c_rating") != null) content.setRating(rs.getBigDecimal("c_rating"));
        content.setGenre(rs.getString("c_genre"));
        content.setSynopsis(rs.getString("c_synopsis"));
        content.setTrailerUrl(rs.getString("c_trailer_url"));
        int runtime = rs.getInt("c_runtime_minutes");
        if (!rs.wasNull()) content.setRuntimeMinutes(runtime);
        if (rs.getTimestamp("c_cached_at") != null) content.setCachedAt(rs.getTimestamp("c_cached_at").toLocalDateTime());

        // Map Reminder
        Reminder reminder = new Reminder();
        reminder.setId(rs.getLong("id"));
        reminder.setUser(user);
        reminder.setContent(content);
        if (rs.getTimestamp("scheduled_at") != null) reminder.setScheduledAt(rs.getTimestamp("scheduled_at").toLocalDateTime());
        if (rs.getString("recurrence") != null) reminder.setRecurrence(Recurrence.valueOf(rs.getString("recurrence")));
        reminder.setMessage(rs.getString("message"));
        if (rs.getString("status") != null) reminder.setStatus(ReminderStatus.valueOf(rs.getString("status")));
        if (rs.getTimestamp("created_at") != null) reminder.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        return reminder;
    }

    @Override
    public Optional<Reminder> findById(Long id) {
        return jdbcClient.sql(SELECT_JOIN + "WHERE r.id = ?")
                .param(id)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public Reminder save(Reminder reminder) {
        if (reminder.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcClient.sql("INSERT INTO reminders (user_id, content_id, scheduled_at, recurrence, message, status, created_at) " +
                            "VALUES (:userId, :contentId, :scheduledAt, :recurrence, :message, :status, :createdAt)")
                    .param("userId", reminder.getUser().getId())
                    .param("contentId", reminder.getContent().getId())
                    .param("scheduledAt", reminder.getScheduledAt())
                    .param("recurrence", reminder.getRecurrence() != null ? reminder.getRecurrence().name() : null)
                    .param("message", reminder.getMessage())
                    .param("status", reminder.getStatus() != null ? reminder.getStatus().name() : null)
                    .param("createdAt", reminder.getCreatedAt())
                    .update(keyHolder);
            if (keyHolder.getKey() != null) {
                reminder.setId(keyHolder.getKey().longValue());
            }
        } else {
            jdbcClient.sql("UPDATE reminders SET user_id = :userId, content_id = :contentId, " +
                            "scheduled_at = :scheduledAt, recurrence = :recurrence, message = :message, " +
                            "status = :status, created_at = :createdAt WHERE id = :id")
                    .param("userId", reminder.getUser().getId())
                    .param("contentId", reminder.getContent().getId())
                    .param("scheduledAt", reminder.getScheduledAt())
                    .param("recurrence", reminder.getRecurrence() != null ? reminder.getRecurrence().name() : null)
                    .param("message", reminder.getMessage())
                    .param("status", reminder.getStatus() != null ? reminder.getStatus().name() : null)
                    .param("createdAt", reminder.getCreatedAt())
                    .param("id", reminder.getId())
                    .update();
        }
        return reminder;
    }

    @Override
    public List<Reminder> findByUserIdOrderByScheduledAtAsc(Long userId) {
        return jdbcClient.sql(SELECT_JOIN + "WHERE r.user_id = ? ORDER BY r.scheduled_at ASC")
                .param(userId)
                .query(this::mapRow)
                .list();
    }

    @Override
    public List<Reminder> findByUserIdAndStatusOrderByScheduledAtAsc(Long userId, ReminderStatus status) {
        return jdbcClient.sql(SELECT_JOIN + "WHERE r.user_id = ? AND r.status = ? ORDER BY r.scheduled_at ASC")
                .param(userId)
                .param(status.name())
                .query(this::mapRow)
                .list();
    }

    @Override
    public Optional<Reminder> findByIdAndUserId(Long id, Long userId) {
        return jdbcClient.sql(SELECT_JOIN + "WHERE r.id = ? AND r.user_id = ?")
                .param(id)
                .param(userId)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public List<Reminder> findByStatusAndScheduledAtLessThanEqual(ReminderStatus status, LocalDateTime dateTime) {
        return jdbcClient.sql(SELECT_JOIN + "WHERE r.status = ? AND r.scheduled_at <= ?")
                .param(status.name())
                .param(dateTime)
                .query(this::mapRow)
                .list();
    }

    @Override
    public long countByUserIdAndStatus(Long userId, ReminderStatus status) {
        return jdbcClient.sql("SELECT COUNT(1) FROM reminders WHERE user_id = ? AND status = ?")
                .param(userId)
                .param(status.name())
                .query(Long.class)
                .single();
    }

    @Override
    public long countByUserId(Long userId) {
        return jdbcClient.sql("SELECT COUNT(1) FROM reminders WHERE user_id = ?")
                .param(userId)
                .query(Long.class)
                .single();
    }
}
