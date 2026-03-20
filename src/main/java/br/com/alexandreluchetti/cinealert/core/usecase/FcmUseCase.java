package br.com.alexandreluchetti.cinealert.core.usecase;

public interface FcmUseCase {

    void sendNotification(String fcmToken, String title, String body);
}
