package net.patterns.saga.orchestratorservice.service;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.order.OrderResponseDTO;
import net.patterns.saga.common.util.Constants;
import net.patterns.saga.common.util.ObjectUtil;
import net.patterns.saga.orchestratorservice.support.DtoConverter;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static io.nats.client.Nats.connect;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRequestListener {
    private final OrchestratorService orchestratorService;

    public void register(String natsServer) {
        log.info("Orchestrator Listener Service Starting");
        try  {
            final Connection nats = connect(natsServer);

            final Dispatcher dispatcher = nats.createDispatcher(msg -> log.debug("Dispatched got: {}", msg));

            dispatcher.subscribe(Constants.ORCHESTRATOR_NATS_ORDER_SUBJECT, Constants.ORDER_QUEUE_NAME, msg ->
                    ObjectUtil.toObject(msg.getData(), OrderResponseDTO.class)
                    .ifPresent(orderRequest -> {
                        log.info("OrchestratorService got msg: {}", msg);
                        orchestratorService.orderProduct(DtoConverter.orderToOrchestratorRequest(orderRequest));
                    }));
        } catch (IOException e) {
            log.error("UNABLE TO CONNECT to NATS server!", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}


