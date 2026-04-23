package br.com.alexandreluchetti.cinealert.configuration.shared;

import br.com.alexandreluchetti.cinealert.core.model.user.User;
import br.com.alexandreluchetti.cinealert.core.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtilImpl jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_noAuthHeader_passesThrough() throws ServletException, IOException {
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).isTokenValid(anyString());
    }

    @Test
    void doFilterInternal_invalidBearerPrefix_passesThrough() throws ServletException, IOException {
        request.addHeader("Authorization", "InvalidPrefix token123");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).isTokenValid(anyString());
    }

    @Test
    void doFilterInternal_invalidToken_passesThrough() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer invalid-token");
        when(jwtUtil.isTokenValid("invalid-token")).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void doFilterInternal_refreshTokenUsedAsAccess_passesThrough() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer refresh-token");
        when(jwtUtil.isTokenValid("refresh-token")).thenReturn(true);
        when(jwtUtil.isRefreshToken("refresh-token")).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void doFilterInternal_validTokenAndUserExists_setsAuthentication() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer valid-token");
        when(jwtUtil.isTokenValid("valid-token")).thenReturn(true);
        when(jwtUtil.isRefreshToken("valid-token")).thenReturn(false);
        when(jwtUtil.extractEmail("valid-token")).thenReturn("test@example.com");

        User user = new User("u-1", "Test", "test@example.com", "pass", null, null, true, null, null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(user);
        verify(filterChain).doFilter(request, response);
    }
}
