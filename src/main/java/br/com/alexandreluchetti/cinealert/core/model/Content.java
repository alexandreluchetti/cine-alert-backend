package br.com.alexandreluchetti.cinealert.core.model;

import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "contents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imdb_id", unique = true, nullable = false, length = 20)
    private String imdbId;

    @Column(nullable = false, length = 300)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ContentType type;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    private Integer year;

    @Column(precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(length = 200)
    private String genre;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Column(name = "trailer_url", length = 500)
    private String trailerUrl;

    @Column(name = "runtime_minutes")
    private Integer runtimeMinutes;

    @Column(name = "cached_at")
    @Builder.Default
    private LocalDateTime cachedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reminder> reminders;
}
