package net.patterns.saga.vendor.service;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.Item;
import net.patterns.saga.common.model.ItemSearchRequest;
import net.patterns.saga.common.util.ObjectUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static io.nats.client.Nats.connect;

@Slf4j
@Service
@RequiredArgsConstructor
public class NatsManager {

    @NonNull
    VendorCall vendorCall;

    public void register(String natsServer) {

        log.debug("Vendor Service Starting");
        try {
            final Connection nats = connect(natsServer);

            final Dispatcher dispatcher = nats.createDispatcher(msg -> {
                log.debug("dispatched: {}", msg);
            });

            dispatcher.subscribe("item.search", msg -> {
                log.debug("Collector got msg: {} with replyTo: {}", msg, msg.getReplyTo());
                ObjectUtil.toObject(msg.getData(), ItemSearchRequest.class)
                        .ifPresent(searchRequest -> {
                            List<Item> items = vendorCall.searchItems(searchRequest);
                            nats.publish(msg.getReplyTo(), ObjectUtil.toBytes(items));
                        });
            });
        } catch (IOException e) {
            log.error("UNABLE TO CONNECT to NATS server!", e);
            return;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}


