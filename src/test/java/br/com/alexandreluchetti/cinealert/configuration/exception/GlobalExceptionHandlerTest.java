package br.com.alexandreluchetti.cinealert.configuration.exception;

import br.com.alexandreluchetti.cinealert.core.exception.AppException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleAppException_returnsCorrectStatusAndMessage() {
        AppException ex = AppException.notFound("User not found");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleAppException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().message()).isEqualTo("User not found");
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().error()).isEqualTo("Not Found");
    }

    @Test
    void handleBadCredentials_returns401() {
        BadCredentialsException ex = new BadCredentialsException("Bad");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleBadCredentials(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().message()).isEqualTo("Invalid email or password");
    }

    @Test
    void handleValidationErrors_returns400WithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError error1 = new FieldError("object", "email", "Must be valid");
        FieldError error2 = new FieldError("object", "password", "Must not be blank");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(error1, error2));

        ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("status")).isEqualTo(400);

        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertThat(fieldErrors).containsEntry("email", "Must be valid");
        assertThat(fieldErrors).containsEntry("password", "Must not be blank");
    }

    @Test
    void handleGenericException_returns500() {
        Exception ex = new RuntimeException("Unexpected runtime issue");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
    }
}
