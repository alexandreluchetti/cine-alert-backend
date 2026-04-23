package br.com.alexandreluchetti.cinealert.configuration.shared;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${app.firebase.credentials-path}")
    private String credentialsPath;

    @Value("${app.firebase.enabled:false}")
    private boolean firebaseEnabled;

    @PostConstruct
    public void initialize() {
        if (!firebaseEnabled) {
            LOGGER.info("Firebase is disabled via configuration.");
            return;
        }

        try {
            if (FirebaseApp.getApps().isEmpty()) {
                LOGGER.info("Initializing Firebase with credentials from: {}", credentialsPath);
                
                InputStream serviceAccount = new FileInputStream(credentialsPath);
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                LOGGER.info("Firebase has been initialized successfully.");
            }
        } catch (IOException e) {
            LOGGER.error("Error initializing Firebase: {}", e.getMessage());
        }
    }
}
