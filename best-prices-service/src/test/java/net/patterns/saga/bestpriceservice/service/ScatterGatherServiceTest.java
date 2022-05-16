package net.patterns.saga.bestpriceservice.service;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Subscription;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.vendor.Item;
import net.patterns.saga.common.util.ObjectUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
class ScatterGatherServiceTest {

    //mvn -Dtest=ScatterGatherServiceTest -DfailIfNoTests=false test

    @TestConfiguration
    public static class WebClientConfiguration {
        @Bean
        public Connection nats() throws IOException, InterruptedException {
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

    @Autowired
    Connection nats;

    @Autowired
    ScatterGatherService service;

    @Test
    void testReceivePricesSync() {
        Subscription sub = nats.subscribe("subject");

        List<Item> items = new ArrayList<>();
        items.add(Item.builder().name("Item1").price(new BigInteger("10")).vendor("TestV1").version("1").build());
        items.add(Item.builder().name("Item2").price(new BigInteger("15")).vendor("TestV2").version("1.1").build());

        nats.publish("subject", "replyto", ObjectUtil.toBytes(items));

        var res = service.receivePrices(sub);

        assertEquals(items.size(), res.size());
        for (int i=0;i<items.size();i++)
            assertEquals(items.get(i), res.get(i));
    }

    /** TOBE completed
    @Test
    void testReceivePricesASync() {
        Subscription sub = nats.subscribe("subject");

        List<Item> items = new ArrayList<>();
        items.add(Item.builder().name("Item1").price(new BigInteger("10")).vendor("TestV1").version("1").build());
        items.add(Item.builder().name("Item2").price(new BigInteger("15")).vendor("TestV2").version("1.1").build());

        nats.publish("subject", "replyto", ObjectUtil.toBytes(items));

        var res = service.receivePrices(sync,sub);

        assertEquals(items.size(), res.size());
        for (int i=0;i<items.size();i++)
            assertEquals(items.get(i), res.get(i));
    }
    **/

}
