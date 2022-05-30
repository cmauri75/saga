package net.patterns.saga.orchestratorservice;

import net.patterns.saga.orchestratorservice.service.OrderRequestListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class OrchestratorServiceApplication {
    @Value("${natServer.url}")
    String natsServer;

    public static void main(String[] args) {
        SpringApplication.run(OrchestratorServiceApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(OrderRequestListener listener) {
        return args -> {
            listener.register(natsServer);
        };
    }
}
