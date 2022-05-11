package net.patterns.saga.bestpriceservice.service;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Subscription;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.Item;
import net.patterns.saga.common.model.ItemSearchRequest;
import net.patterns.saga.common.model.ItemSearchResponse;
import net.patterns.saga.common.util.ObjectUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.time.Duration;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ScatterGatherService {

    private Connection nats;

    public Mono<ItemSearchResponse> broadcastAsync(ItemSearchRequest itemSearchRequest) {
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

    public ItemSearchResponse broadcastSync(ItemSearchRequest itemSearchRequest) {

        String inbox = nats.createInbox();
        Subscription subscription = nats.subscribe(inbox);

        //ask for results
        nats.publish("item.search", inbox, ObjectUtil.toBytes(itemSearchRequest));

        //receive and collect
        var itemList = receivePrices(subscription);
        itemList.sort(Comparator.comparing(Item::getPrice));
        var res = ItemSearchResponse.builder().itemSearchRequest(itemSearchRequest).itemList(itemList).build();

        //unsubscribe
        subscription.unsubscribe();

        return res;
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

    protected List<Item> receivePrices(Subscription subscription) {
        List<Item> ret = new ArrayList<>();

        try {
            while (true) {
                Message message = subscription.nextMessage(Duration.ofSeconds(1));
                if (message == null) return ret;
                Optional<Item[]> items = ObjectUtil.toObject(message.getData(), Item[].class);
                items.ifPresent(value -> ret.addAll(Arrays.asList(value)));
            }
        } catch (InterruptedException e) {
            log.error("Process interrupted", e);
        } finally {
            log.debug("returning {} elements", ret.size());
        }
        return ret;
    }

}
