package me.github.lparo.geolocation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class GeolocationApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeolocationApplication.class, args);
    }
}