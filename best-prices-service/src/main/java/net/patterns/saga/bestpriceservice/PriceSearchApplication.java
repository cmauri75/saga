package net.patterns.saga.bestpriceservice;

import io.nats.client.Connection;
import io.nats.client.Nats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@Slf4j
@SpringBootApplication
public class PriceSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(PriceSearchApplication.class, args);
    }

    @Bean
    public Connection nats(@Value("${natServer.url}") String natsServer) throws IOException, InterruptedException {
        return Nats.connect(natsServer);
    }

}
