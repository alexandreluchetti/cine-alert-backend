package br.com.alexandreluchetti.cinealert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CineAlertApplication {
    public static void main(String[] args) {
        SpringApplication.run(CineAlertApplication.class, args);
    }
}
