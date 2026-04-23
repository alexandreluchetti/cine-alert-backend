package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class FcmUseCaseImplTest {

    @Test
    void sendNotification_firebaseDisabled_logsOnly() {
        FcmUseCaseImpl fcmUseCase = new FcmUseCaseImpl(false);

        assertThatCode(() -> fcmUseCase.sendNotification("token", "title", "body"))
                .doesNotThrowAnyException();
    }

    @Test
    void sendNotification_nullToken_skips() {
        FcmUseCaseImpl fcmUseCase = new FcmUseCaseImpl(true);

        assertThatCode(() -> fcmUseCase.sendNotification(null, "title", "body"))
                .doesNotThrowAnyException();
    }

    @Test
    void sendNotification_blankToken_skips() {
        FcmUseCaseImpl fcmUseCase = new FcmUseCaseImpl(true);

        assertThatCode(() -> fcmUseCase.sendNotification("   ", "title", "body"))
                .doesNotThrowAnyException();
    }

    @Test
    void sendNotification_validToken_attemptsToSendAndCatchesException() {
        // Since FirebaseApp is not initialized in tests, sending will throw an exception
        // inside the try block, but FcmUseCaseImpl catches and logs it.
        FcmUseCaseImpl fcmUseCase = new FcmUseCaseImpl(true);

        assertThatCode(() -> fcmUseCase.sendNotification("valid-token", "title", "body"))
                .doesNotThrowAnyException();
    }
}
