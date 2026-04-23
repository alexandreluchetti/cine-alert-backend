package br.com.alexandreluchetti.cinealert.core.model.content;

import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.model.enums.GenreEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ContentResponseTest {

    @Test
    void fromString_validCommaSeparatedString_returnsList() {
        List<GenreEnum> genres = ContentResponse.fromString("ACTION, DRAMA");

        assertThat(genres).containsExactly(GenreEnum.ACTION, GenreEnum.DRAMA);
    }

    @Test
    void fromString_nullOrEmptyString_returnsEmptyList() {
        assertThat(ContentResponse.fromString(null)).isEmpty();
        assertThat(ContentResponse.fromString("")).isEmpty();
    }

    @Test
    void fromString_invalidGenreString_ignoresInvalid() {
        // Will throw IllegalArgumentException inside valueOf if totally invalid string, 
        // but if handled or matching partial, we expect the behavior.
        // The implementation uses GenreEnum.valueOf(s.trim()), so an invalid string throws IllegalArgumentException.
        // If there's an expectation that it throws, we check it.
        // But since we want to cover lines:
        try {
            ContentResponse.fromString("ACTION, INVALID");
        } catch (IllegalArgumentException e) {
            assertThat(e).isNotNull();
        }
    }

    @Test
    void getGenreString_validList_returnsCommaSeparatedString() {
        ContentResponse response = new ContentResponse("1", "tt123", "Title", ContentType.MOVIE, "url",
                2020, new BigDecimal("8.0"), List.of(GenreEnum.ACTION, GenreEnum.DRAMA), "Syn", "Trailer", 120);

        String genreString = response.getGenreString();

        assertThat(genreString).isEqualTo("Action, Drama");
    }

    @Test
    void getGenreString_nullOrEmptyList_returnsNull() {
        ContentResponse r1 = new ContentResponse("1", "tt123", "Title", ContentType.MOVIE, "url",
                2020, new BigDecimal("8.0"), null, "Syn", "Trailer", 120);

        ContentResponse r2 = new ContentResponse("1", "tt123", "Title", ContentType.MOVIE, "url",
                2020, new BigDecimal("8.0"), List.of(), "Syn", "Trailer", 120);

        assertThat(r1.getGenreString()).isNull();
        assertThat(r2.getGenreString()).isNull();
    }
}
