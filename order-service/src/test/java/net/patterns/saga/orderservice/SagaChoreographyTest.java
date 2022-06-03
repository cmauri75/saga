package net.patterns.saga.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Subscription;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.order.OrderRequestDTO;
import net.patterns.saga.common.model.order.OrderResponseDTO;
import net.patterns.saga.common.model.order.OrderStatus;
import net.patterns.saga.common.util.Constants;
import net.patterns.saga.common.util.ObjectUtil;
import net.patterns.saga.orderservice.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Import(OrderServiceApplicationTest.class)
@SpringBootTest
class SagaChoreographyTest {
    //mvn -Dtest=SagaChoreographyTest -DfailIfNoTests=false test

    //Starts a nat server
    @TestConfiguration
    public static class WebClientConfiguration {
        @Bean
        public Connection nats() throws IOException, InterruptedException {
            return OrderServiceApplicationTest.nats();
        }
    }

    @Autowired
    Connection nats;

    @Autowired
    OrderService service;

    OrderRequestDTO testOrder = OrderRequestDTO.builder()
            .orderId(UUID.randomUUID())
            .userId(-1)
            .productId(-1)
            .build();

    /**
     * Just checks if message sent from OrderService are received only one in a queue, so in case multiple inventory/payment service instance, only one will react
     *
     * @throws InterruptedException
     */
    @Test
    void testChoreoOrder() throws InterruptedException {
        Subscription resQueuePay = nats.subscribe(Constants.CHOREOGRAPHY_NATS_ORDER_EVENTS, Constants.PAYMENTS_QUEUE_NAME);
        Subscription resQueueInv = nats.subscribe(Constants.CHOREOGRAPHY_NATS_ORDER_EVENTS, Constants.INVENTORY_QUEUE_NAME);

        //sends message to payment and inventory services
        service.createOrderSagaChoreography(testOrder);


        Message payMessage = resQueuePay.nextMessage(Duration.ofSeconds(1));
        if (payMessage != null) {
            log.info("received payMessage");
        }

        Message invMessage = resQueueInv.nextMessage(Duration.ofSeconds(1));
        if (invMessage != null) {
            log.info("received invMessage");
        }

        Assertions.assertNotNull(payMessage);
        Assertions.assertNotNull(invMessage);
    }

}
