package net.patterns.saga.bestpriceservice.service;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Subscription;
import io.nats.client.support.Status;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.patterns.saga.common.grpc.StoreRequest;
import net.patterns.saga.common.grpc.StoreResponse;
import net.patterns.saga.common.grpc.StoringServiceGrpc;
import net.patterns.saga.common.model.vendor.Item;
import net.patterns.saga.common.model.vendor.ItemSearchRequest;
import net.patterns.saga.common.model.vendor.ItemSearchResponse;
import net.patterns.saga.common.model.storing.ItemConverter;
import net.patterns.saga.common.model.storing.StoreCounter;
import net.patterns.saga.common.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScatterGatherService {

    @NonNull
    private final Connection nats;

    @Value("${storeservice.url}")
    private String storeServiceUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @GrpcClient("storing-grpc-service")
    StoringServiceGrpc.StoringServiceBlockingStub storingServiceStub;


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
                .doOnNext(i -> {
                    Item firstItem = i.getItemList().get(0);

                    StoreRequest storeRequest = StoreRequest.newBuilder()
                            .setItem(ItemConverter.toGrpcItem(firstItem))
                            .build();
                    StoreResponse result = storingServiceStub.store(storeRequest);

                    log.info("Grpc called: {}", result);

                    subscription.unsubscribe();
                });
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

        //update store service
        StoreCounter result = restTemplate.postForObject(storeServiceUrl + "/store", itemList.get(0), StoreCounter.class);
        log.info("Stores are: " + result);

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
                log.info("---> {}", message);
                if (message == null || (message.getStatus() != null && message.getStatus().getCode() == Status.NO_RESPONDERS_CODE))
                    return ret;
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
