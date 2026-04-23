package br.com.alexandreluchetti.cinealert.core.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppExceptionTest {

    @Test
    void badRequest_createsExceptionWith400Status() {
        AppException ex = AppException.badRequest("Bad Request Message");

        assertThat(ex.getMessage()).isEqualTo("Bad Request Message");
        assertThat(ex.getStatus().value()).isEqualTo(400);
    }

    @Test
    void unauthorized_createsExceptionWith401Status() {
        AppException ex = AppException.unauthorized("Unauthorized Message");

        assertThat(ex.getMessage()).isEqualTo("Unauthorized Message");
        assertThat(ex.getStatus().value()).isEqualTo(401);
    }

    @Test
    void forbidden_createsExceptionWith403Status() {
        AppException ex = AppException.forbidden("Forbidden Message");

        assertThat(ex.getMessage()).isEqualTo("Forbidden Message");
        assertThat(ex.getStatus().value()).isEqualTo(403);
    }

    @Test
    void notFound_createsExceptionWith404Status() {
        AppException ex = AppException.notFound("Not Found Message");

        assertThat(ex.getMessage()).isEqualTo("Not Found Message");
        assertThat(ex.getStatus().value()).isEqualTo(404);
    }

    @Test
    void conflict_createsExceptionWith409Status() {
        AppException ex = AppException.conflict("Conflict Message");

        assertThat(ex.getMessage()).isEqualTo("Conflict Message");
        assertThat(ex.getStatus().value()).isEqualTo(409);
    }
}
