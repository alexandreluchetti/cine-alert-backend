package br.com.alexandreluchetti.cinealert.dataprovider.repository;

import br.com.alexandreluchetti.cinealert.core.model.User;
import br.com.alexandreluchetti.cinealert.core.repository.UserRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcClient jdbcClient;

    public UserRepositoryImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setFcmToken(rs.getString("fcm_token"));
        user.setActive(rs.getBoolean("active"));
        if (rs.getTimestamp("created_at") != null) {
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return jdbcClient.sql("SELECT * FROM users WHERE id = ?")
                .param(id)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcClient.sql("INSERT INTO users (name, email, password, avatar_url, fcm_token, active, created_at, updated_at) " +
                            "VALUES (:name, :email, :password, :avatarUrl, :fcmToken, :active, :createdAt, :updatedAt)")
                    .param("name", user.getName())
                    .param("email", user.getEmail())
                    .param("password", user.getPassword())
                    .param("avatarUrl", user.getAvatarUrl())
                    .param("fcmToken", user.getFcmToken())
                    .param("active", user.isActive())
                    .param("createdAt", user.getCreatedAt())
                    .param("updatedAt", user.getUpdatedAt())
                    .update(keyHolder);
            if (keyHolder.getKey() != null) {
                user.setId(keyHolder.getKey().longValue());
            }
        } else {
            jdbcClient.sql("UPDATE users SET name = :name, email = :email, password = :password, " +
                            "avatar_url = :avatarUrl, fcm_token = :fcmToken, active = :active, " +
                            "created_at = :createdAt, updated_at = :updatedAt WHERE id = :id")
                    .param("name", user.getName())
                    .param("email", user.getEmail())
                    .param("password", user.getPassword())
                    .param("avatarUrl", user.getAvatarUrl())
                    .param("fcmToken", user.getFcmToken())
                    .param("active", user.isActive())
                    .param("createdAt", user.getCreatedAt())
                    .param("updatedAt", user.getUpdatedAt())
                    .param("id", user.getId())
                    .update();
        }
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jdbcClient.sql("SELECT * FROM users WHERE email = ?")
                .param(email)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public boolean existsByEmail(String email) {
        return jdbcClient.sql("SELECT COUNT(1) FROM users WHERE email = ?")
                .param(email)
                .query(Integer.class)
                .single() > 0;
    }
}
