package net.patterns.saga.vendor;

import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.vendor.service.NatsManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class ItemVendorApplication {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${natServer.url}")
    String natsServer;

    public static void main(String[] args) {
        SpringApplication.run(ItemVendorApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(NatsManager natsManager) {
        if ("test".equals(activeProfile))
            return null;

        return args -> {
            natsManager.register(natsServer);
        };
    }

}
