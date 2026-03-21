package br.com.alexandreluchetti.cinealert.configuration.resources;

import br.com.alexandreluchetti.cinealert.configuration.shared.JwtUtil;
import br.com.alexandreluchetti.cinealert.core.repository.ContentRepository;
import br.com.alexandreluchetti.cinealert.core.repository.ReminderRepository;
import br.com.alexandreluchetti.cinealert.core.service.ImdbService;
import br.com.alexandreluchetti.cinealert.core.usecase.*;
import br.com.alexandreluchetti.cinealert.core.usecase.impl.*;
import br.com.alexandreluchetti.cinealert.dataprovider.imdb.ImdbServiceImpl;
import br.com.alexandreluchetti.cinealert.dataprovider.repository.ContentRepositoryImpl;
import br.com.alexandreluchetti.cinealert.dataprovider.repository.UserRepository;
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

    @Bean
    public ContentUseCase loadContentUseCase(
            ImdbServiceImpl imdbServiceImpl,
            ContentRepository contentRepository
    ) {
        return new ContentUseCaseImpl(imdbServiceImpl, contentRepository);
    }

    @Bean
    public FcmUseCase loadFcmUseCase() {
        return new FcmUseCaseImpl();
    }

    @Bean
    public NotificationSchedulerUseCase loadNotificationSchedulerUseCase(
            ReminderRepository reminderRepository,
            FcmUseCase fcmUseCase
    ) {
        return new NotificationSchedulerUseCaseImpl(reminderRepository, fcmUseCase);
    }

    @Bean
    public ReminderUseCase loadReminderUseCase(
            ReminderRepository reminderRepository,
            ContentRepository contentRepository
    ) {
        return new ReminderUseCaseImpl(reminderRepository, contentRepository);
    }

    @Bean
    public UserUseCase loadUserUseCase(
            UserRepository userRepository,
            ReminderRepository reminderRepository,
            PasswordEncoder passwordEncoder
    ) {
        return new UserUseCaseImpl(userRepository, reminderRepository, passwordEncoder);
    }

    @Bean
    public ImdbService loadImdbService() {
        return new ImdbServiceImpl();
    }
}
