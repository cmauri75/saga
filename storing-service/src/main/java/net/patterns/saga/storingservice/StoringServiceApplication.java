package net.patterns.saga.storingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class StoringServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoringServiceApplication.class, args);
    }

}
