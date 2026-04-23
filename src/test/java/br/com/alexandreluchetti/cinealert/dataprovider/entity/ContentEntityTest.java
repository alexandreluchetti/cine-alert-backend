package br.com.alexandreluchetti.cinealert.dataprovider.entity;

import br.com.alexandreluchetti.cinealert.core.model.content.Content;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ContentEntityTest {

    @Test
    void fromModel_toModel_roundTrip() {
        LocalDateTime now = LocalDateTime.now();
        Content model = new Content("id-1", "tt123", "Title", ContentType.MOVIE, "url",
                2020, new BigDecimal("8.0"), "Action", "Syn", "Trailer", 120, now);

        ContentEntity entity = ContentEntity.fromModel(model);

        assertThat(entity.getId()).isEqualTo("id-1");
        assertThat(entity.getImdbId()).isEqualTo("tt123");

        Content backToModel = entity.toModel();

        assertThat(backToModel.getId()).isEqualTo("id-1");
        assertThat(backToModel.getImdbId()).isEqualTo("tt123");
        assertThat(backToModel.getType()).isEqualTo(ContentType.MOVIE);
        assertThat(backToModel.getCachedAt()).isEqualTo(now);
    }
}
