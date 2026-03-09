package com.cinealert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CineAlertApplication {
    public static void main(String[] args) {
        SpringApplication.run(CineAlertApplication.class, args);
    }
}
