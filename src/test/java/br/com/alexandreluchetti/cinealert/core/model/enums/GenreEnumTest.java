package br.com.alexandreluchetti.cinealert.core.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenreEnumTest {

    @Test
    void fromValue_validValueIgnoreCase_returnsEnum() {
        assertThat(GenreEnum.fromValue("Action")).isEqualTo(GenreEnum.ACTION);
        assertThat(GenreEnum.fromValue("action")).isEqualTo(GenreEnum.ACTION);
        assertThat(GenreEnum.fromValue("SCI-FI")).isEqualTo(GenreEnum.SCI_FI);
    }

    @Test
    void fromValue_invalidValue_returnsNull() {
        assertThat(GenreEnum.fromValue("Invalid")).isNull();
    }

    @Test
    void getValue_returnsCorrectString() {
        assertThat(GenreEnum.ROMANCE.getValue()).isEqualTo("Romance");
    }
}
