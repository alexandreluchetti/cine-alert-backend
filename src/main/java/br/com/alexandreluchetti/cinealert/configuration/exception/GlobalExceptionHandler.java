package br.com.alexandreluchetti.cinealert.configuration.exception;

import br.com.alexandreluchetti.cinealert.core.exception.AppException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(int status, String error, String message, LocalDateTime timestamp) {}

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex, HttpServletResponse response) {
        if (response.isCommitted()) {
            log.debug("Response already committed, skipping AppException handler for: {}", ex.getMessage());
            return null;
        }
        log.warn("AppException occurred: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
            .body(new ErrorResponse(
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now()
            ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Bad credentials attempt");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(401, "Unauthorized", "Invalid email or password", LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            fieldErrors.put(field, error.getDefaultMessage());
        });

        log.warn("Validation Failed. fieldErrors: {}", fieldErrors);

        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Validation Failed");
        body.put("fieldErrors", fieldErrors);
        body.put("timestamp", LocalDateTime.now());

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Trata desconexão abrupta do cliente (ex: mobile sai da tela durante request lento).
     * ClientAbortException NÃO é um erro de aplicação — não loga como ERROR.
     */
    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbort(ClientAbortException ex) {
        log.debug("Client closed connection before response was complete: {}", ex.getMessage());
        // Não tenta escrever resposta — conexão já está fechada
    }

    /**
     * Fallback para IOExceptions genéricas que indicam conexão encerrada pelo cliente.
     */
    @ExceptionHandler(IOException.class)
    public void handleIOException(IOException ex, HttpServletResponse response) {
        String msg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
        if (msg.contains("connection reset") || msg.contains("broken pipe") || msg.contains("connection aborted")) {
            log.debug("Client disconnected during response: {}", ex.getMessage());
            return;
        }
        log.error("Unexpected IO error: ", ex);
        if (!response.isCommitted()) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex, HttpServletResponse response) {
        if (response.isCommitted()) {
            return null;
        }
        log.debug("No resource found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(404, "Not Found", "Resource not found", LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletResponse response) {
        // Verifica se o response já foi comprometido (headers/body já enviados ao cliente)
        if (response.isCommitted()) {
            log.debug("Response already committed, cannot write error body. Exception: {}", ex.getMessage());
            return null;
        }

        log.error("An unexpected error occurred: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred", LocalDateTime.now()));
    }
}
