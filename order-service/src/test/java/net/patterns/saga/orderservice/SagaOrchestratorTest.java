package net.patterns.saga.orderservice;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.Subscription;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.order.OrderRequestDTO;
import net.patterns.saga.common.model.order.OrderStatus;
import net.patterns.saga.common.util.Constants;
import net.patterns.saga.common.util.ObjectUtil;
import net.patterns.saga.orderservice.entity.PurchaseOrder;
import net.patterns.saga.orderservice.service.OrderService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@Slf4j
@Import(OrderServiceApplicationTest.class)
@SpringBootTest
class SagaOrchestratorTest  {
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

    @Test
    void testReceivePricesSync() throws InterruptedException {
        Subscription sub = nats.subscribe(Constants.ORCHESTRATOR_NATS_TOPIC);

        OrderRequestDTO order = OrderRequestDTO.builder()
                .orderId(UUID.randomUUID())
                .userId(-1)
                .productId(-1)
                .build();

        service.createOrderSagaOrchestration(order);

        Message mess = sub.nextMessage(Duration.ofSeconds(5));
        Optional<PurchaseOrder> opOrder = ObjectUtil.toObject(mess.getData(), PurchaseOrder.class);

        Assert.assertTrue(opOrder.isPresent());

        PurchaseOrder pOrder = opOrder.get();

        assertEquals(order.getUserId(), pOrder.getUserId());
        assertEquals(order.getProductId(), pOrder.getProductId());
        assertEquals(OrderStatus.CREATED, pOrder.getStatus());

    }


}
