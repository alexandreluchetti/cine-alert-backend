package br.com.alexandreluchetti.cinealert.configuration.resources;

import br.com.alexandreluchetti.cinealert.configuration.shared.JwtUtil;
import br.com.alexandreluchetti.cinealert.core.usecase.AuthUseCase;
import br.com.alexandreluchetti.cinealert.core.usecase.impl.AuthUseCaseImpl;
import br.com.alexandreluchetti.cinealert.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ResourcesConfiguration {

    @Bean
    public AuthUseCase loadAuthUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        return new AuthUseCaseImpl(userRepository, passwordEncoder, jwtUtil);
    }
}
