package br.com.alexandreluchetti.cinealert.entrypoint.dto.content;

import br.com.alexandreluchetti.cinealert.core.model.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.model.enums.GenreEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ContentResponseDtoTest {

    @Test
    void fromModel() {
        ContentResponse model = new ContentResponse("1", "tt123", "Title", ContentType.MOVIE, "url",
                2020, new BigDecimal("8.0"), List.of(GenreEnum.ACTION), "Syn", "Trailer", 120);

        ContentResponseDto dto = ContentResponseDto.fromModel(model);

        assertThat(dto.id()).isEqualTo("1");
        assertThat(dto.genre()).isEqualTo("Action");
    }
}
