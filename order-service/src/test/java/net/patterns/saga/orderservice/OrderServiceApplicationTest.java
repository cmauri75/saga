package net.patterns.saga.orderservice;

import io.nats.client.Connection;
import io.nats.client.Nats;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

@Slf4j
//@TestConfiguration
public class OrderServiceApplicationTest {

    //@Bean
    public static Connection nats() throws IOException, InterruptedException {
        log.info("Setting up");
        GenericContainer natsContainer = new GenericContainer(DockerImageName.parse("nats:alpine"))
                .withExposedPorts(4222);

        natsContainer.start();

        String address = natsContainer.getHost();
        Integer port = natsContainer.getFirstMappedPort();

        String finalUrl = "nats://" + address + ":" + port;
        log.info("nats working on: {}", finalUrl);

        return Nats.connect(finalUrl);
    }


}
