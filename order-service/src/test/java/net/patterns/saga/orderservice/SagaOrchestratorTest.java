package net.patterns.saga.orderservice;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Import(OrderServiceApplicationTest.class)
@SpringBootTest
class SagaOrchestratorTest {
    //mvn -Dtest=SagaOrchestratorTest -DfailIfNoTests=false test


    //Unfortunally external testconfig does not works
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
     * @throws InterruptedException
     */
    @Test
    void testReceiveSinglePricesQueueSync() throws InterruptedException {
        Subscription queue1 = nats.subscribe(Constants.ORCHESTRATOR_NATS_ORDER_SUBJECT, Constants.ORDER_QUEUE_NAME);
        Subscription queue2 = nats.subscribe(Constants.ORCHESTRATOR_NATS_ORDER_SUBJECT, Constants.ORDER_QUEUE_NAME);

        service.createOrderSagaOrchestration(testOrder);

        List<Message> messages = new ArrayList<>();

        Message message1 = queue1.nextMessage(Duration.ofSeconds(1));
        if (message1 != null) {
            log.info("received msg on sub1");
            messages.add(message1);
        }

        Message message2 = queue2.nextMessage(Duration.ofSeconds(1));
        if (message2 != null) {
            log.info("received msg on sub2");
            messages.add(message2);
        }

        Assertions.assertEquals(1, messages.size());

        Optional<OrderResponseDTO> opOrder = ObjectUtil.toObject(messages.get(0).getData(), OrderResponseDTO.class);

        Assertions.assertTrue(opOrder.isPresent());

        OrderResponseDTO pOrder = opOrder.get();

        Assertions.assertEquals(testOrder.getUserId(), pOrder.getUserId());
        Assertions.assertEquals(testOrder.getProductId(), pOrder.getProductId());
        Assertions.assertEquals(OrderStatus.CREATED, pOrder.getStatus());
    }

}
