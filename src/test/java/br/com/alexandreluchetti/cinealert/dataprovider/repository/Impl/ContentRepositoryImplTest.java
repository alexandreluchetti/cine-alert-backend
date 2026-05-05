package br.com.alexandreluchetti.cinealert.dataprovider.repository.Impl;

import br.com.alexandreluchetti.cinealert.core.model.content.Content;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.dataprovider.entity.ContentEntity;
import br.com.alexandreluchetti.cinealert.dataprovider.repository.ContentMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentRepositoryImplTest {

    @Mock
    private ContentMongoRepository contentMongoRepository;

    private ContentRepositoryImpl contentRepositoryImpl;

    @BeforeEach
    void setUp() {
        contentRepositoryImpl = new ContentRepositoryImpl(contentMongoRepository);
    }

    @Test
    void findByImdbId_returnsContent() {
        ContentEntity entity = buildContentEntity();
        when(contentMongoRepository.findFirstByImdbId("tt123")).thenReturn(Optional.of(entity));

        Optional<Content> result = contentRepositoryImpl.findByImdbId("tt123");

        assertThat(result).isPresent();
        assertThat(result.get().getImdbId()).isEqualTo("tt123");
    }

    @Test
    void existsByImdbId_returnsBoolean() {
        when(contentMongoRepository.existsByImdbId("tt123")).thenReturn(true);

        boolean result = contentRepositoryImpl.existsByImdbId("tt123");

        assertThat(result).isTrue();
    }

    @Test
    void findById_returnsContent() {
        ContentEntity entity = buildContentEntity();
        when(contentMongoRepository.findById("id-1")).thenReturn(Optional.of(entity));

        Optional<Content> result = contentRepositoryImpl.findById("id-1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("id-1");
    }

    @Test
    void save_savesAndReturnsContent() {
        ContentEntity entity = buildContentEntity();
        when(contentMongoRepository.save(any(ContentEntity.class))).thenReturn(entity);

        Content content = entity.toModel();
        Content result = contentRepositoryImpl.save(content);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("id-1");
        verify(contentMongoRepository).save(any(ContentEntity.class));
    }

    private ContentEntity buildContentEntity() {
        return ContentEntity.builder()
                .id("id-1")
                .imdbId("tt123")
                .title("Title")
                .type(ContentType.MOVIE)
                .posterUrl("url")
                .year(2020)
                .rating(new BigDecimal("8.0"))
                .genre("Action")
                .synopsis("Syn")
                .trailerUrl("trailer")
                .runtimeMinutes(120)
                .cachedAt(LocalDateTime.now())
                .build();
    }
}
