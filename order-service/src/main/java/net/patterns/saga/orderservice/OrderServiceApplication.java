package net.patterns.saga.orderservice;

import io.nats.client.Connection;
import io.nats.client.Nats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@SpringBootApplication
@Slf4j
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    public Connection nats(@Value("${natServer.url}") String natsServer) throws InterruptedException {
        try {
            return Nats.connect(natsServer);
        } catch (IOException e) {
            log.info("Unable to connect to server: {}",natsServer);
            return null;
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
