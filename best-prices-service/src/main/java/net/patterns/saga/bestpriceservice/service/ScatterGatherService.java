package net.patterns.saga.bestpriceservice.service;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Subscription;
import lombok.AllArgsConstructor;
import net.patterns.saga.common.model.Item;
import net.patterns.saga.common.model.ItemSearchRequest;
import net.patterns.saga.common.model.ItemSearchResponse;
import net.patterns.saga.common.util.ObjectUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ScatterGatherService {

    private Connection nats;

    public Mono<ItemSearchResponse> broadcast(ItemSearchRequest itemSearchRequest) {

        String inbox = nats.createInbox();
        Subscription subscription = nats.subscribe(inbox);

        return Flux.generate((SynchronousSink<Item[]> fluxSink) -> receivePrices(fluxSink, subscription))
                .flatMap(Flux::fromArray)
                .bufferTimeout(5, Duration.ofSeconds(1))
                .map(list -> {
                    list.sort(Comparator.comparing(Item::getPrice));
                    return list;
                })
                .map(list -> ItemSearchResponse.builder().itemSearchRequest(itemSearchRequest).itemList(list).build())
                .next()
                .doFirst(() -> nats.publish("item.search", inbox, ObjectUtil.toBytes(itemSearchRequest)))
                .doOnNext(i -> subscription.unsubscribe());
    }

    private void receivePrices(SynchronousSink<Item[]> synchronousSink, Subscription subscription) {
        try {
            Message message = subscription.nextMessage(Duration.ofSeconds(1));
            Optional<Item[]> items = ObjectUtil.toObject(message.getData(), Item[].class);
            items.ifPresent(synchronousSink::next);
        } catch (Exception e) {
            synchronousSink.error(e);
        }
    }

}
