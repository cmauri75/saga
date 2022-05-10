package net.patterns.saga.vendor.service;

import net.patterns.saga.common.model.Item;
import net.patterns.saga.common.model.ItemSearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Service
public class VendorCall {
    @Value("${vendor.name}")
    String vendorName;

    public static final Random RANDOM = new Random();

    public List<Item> searchItems(ItemSearchRequest searchRequest) {
        int randInt = RANDOM.nextInt(2);
        return IntStream.rangeClosed(1, randInt+1)
                .mapToObj(i -> createItem(searchRequest.getItem())).toList();
    }

    private Item createItem(String item) {
        return Item.builder()
                .name(item)
                .price(new BigInteger(String.valueOf(RANDOM.nextInt(10000))))
                .vendor(vendorName)
                .version(String.valueOf(RANDOM.nextInt(10)))
                .build();
    }
}
