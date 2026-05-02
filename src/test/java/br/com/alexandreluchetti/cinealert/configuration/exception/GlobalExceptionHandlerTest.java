package br.com.alexandreluchetti.cinealert.configuration.exception;

import br.com.alexandreluchetti.cinealert.core.exception.AppException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.ClientAbortException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ─────────────────────── AppException ───────────────────────

    @Test
    void handleAppException_returnsCorrectStatusAndMessage() {
        AppException ex = AppException.notFound("User not found");
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.isCommitted()).thenReturn(false);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> result = handler.handleAppException(ex, response);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody().message()).isEqualTo("User not found");
        assertThat(result.getBody().status()).isEqualTo(404);
        assertThat(result.getBody().error()).isEqualTo("Not Found");
    }

    @Test
    void handleAppException_committedResponse_returnsNull() {
        AppException ex = AppException.notFound("User not found");
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.isCommitted()).thenReturn(true);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> result = handler.handleAppException(ex, response);

        assertThat(result).isNull();
    }

    // ─────────────────────── BadCredentials ───────────────────────

    @Test
    void handleBadCredentials_returns401() {
        BadCredentialsException ex = new BadCredentialsException("Bad");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleBadCredentials(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().message()).isEqualTo("Invalid email or password");
    }

    // ─────────────────────── Validation ───────────────────────

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

    // ─────────────────────── ClientAbortException ───────────────────────

    @Test
    void handleClientAbort_doesNotThrow() {
        // Deve ser silencioso — não é erro de aplicação
        ClientAbortException ex = new ClientAbortException();
        handler.handleClientAbort(ex);
        // Sem exceção = comportamento correto
    }

    // ─────────────────────── IOException ───────────────────────

    @Test
    void handleIOException_connectionReset_isIgnored() {
        IOException ex = new IOException("Connection reset by peer");
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.isCommitted()).thenReturn(false);

        handler.handleIOException(ex, response);
        // Sem exceção — cliente desconectou, comportamento esperado
    }

    @Test
    void handleIOException_brokenPipe_isIgnored() {
        IOException ex = new IOException("Broken pipe");
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.isCommitted()).thenReturn(false);

        handler.handleIOException(ex, response);
    }

    @Test
    void handleIOException_otherIO_sets500WhenNotCommitted() {
        IOException ex = new IOException("Disk read error");
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.isCommitted()).thenReturn(false);

        handler.handleIOException(ex, response);

        // Verifica que tenta setar 500 quando não é desconexão do cliente
        org.mockito.Mockito.verify(response).setStatus(500);
    }

    // ─────────────────────── Generic Exception ───────────────────────

    @Test
    void handleGenericException_returns500() {
        Exception ex = new RuntimeException("Unexpected runtime issue");
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.isCommitted()).thenReturn(false);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> result = handler.handleGenericException(ex, response);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody().status()).isEqualTo(500);
        assertThat(result.getBody().message()).isEqualTo("An unexpected error occurred");
    }

    @Test
    void handleGenericException_committedResponse_returnsNull() {
        Exception ex = new RuntimeException("Error after partial write");
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.isCommitted()).thenReturn(true);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> result = handler.handleGenericException(ex, response);

        assertThat(result).isNull();
    }
}
