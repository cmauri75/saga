package net.patterns.saga.vendor;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.vendor.ItemSearchRequest;
import net.patterns.saga.common.util.ObjectUtil;
import net.patterns.saga.vendor.service.NatsManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@Testcontainers
@SpringBootTest
class ItemVendorServiceTest {

    private String finalUrl = "";
    private Connection natsConn;

    @Autowired
    NatsManager natsManager;

    @Container
    public GenericContainer natsContainer = new GenericContainer(DockerImageName.parse("nats:alpine"))
            .withExposedPorts(4222);

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        log.info("Setting up");
        String address = natsContainer.getHost();
        Integer port = natsContainer.getFirstMappedPort();

        finalUrl = "nats://" + address + ":" + port;
        log.info("nats working on: {}", finalUrl);

        natsConn = Nats.connect(finalUrl);
    }

    @Test
    void testService() throws Exception {
        natsManager.register(finalUrl);

        //Creating connection
        String inbox = natsConn.createInbox();
        log.debug("Created inbox: {}", inbox);

        String itemName = "Casa" + System.currentTimeMillis();
        ItemSearchRequest searchRequest = ItemSearchRequest.builder().item(itemName).build();
        log.debug("sending message: {}", itemName);

        //Registering for response
        final Dispatcher dispatcher = natsConn.createDispatcher(msg -> {
        });

        AtomicReference<String> vendor = new AtomicReference<>("");
        AtomicReference<String> receivedItemName = new AtomicReference<>("");
        dispatcher.subscribe(inbox, msg -> {
            ObjectUtil.toObject(msg.getData(), List.class)
                    .ifPresent(items -> {
                        Map<String, String> item = (Map<String, String>) items.get(0);
                        receivedItemName.set(item.get("name"));
                        vendor.set(item.get("vendor"));
                    });
        });

        //sending message
        natsConn.publish("item.search", inbox, ObjectUtil.toBytes(searchRequest));

        //waiting for result
        Thread.sleep(1000);

        //Testing resuls
        assertEquals(itemName, receivedItemName.get());
        assertEquals("TEST", vendor.get());
    }
}
