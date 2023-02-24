package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class StatService {

    private static final String port = "9090";

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(StatService.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", port));
        app.run(args);
    }

}