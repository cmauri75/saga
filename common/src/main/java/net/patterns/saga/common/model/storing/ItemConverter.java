package net.patterns.saga.common.model.storing;

import net.patterns.saga.common.model.vendor.Item;

import java.math.BigInteger;

public class ItemConverter {

    private ItemConverter() {
    }

    public static Item toItem(net.patterns.saga.common.grpc.Item grpcItem) {
        return Item.builder()
                .name(grpcItem.getName())
                .vendor(grpcItem.getVendor())
                .version(grpcItem.getVersion())
                .price(BigInteger.valueOf(grpcItem.getPrice()))
                .build();
    }

    public static net.patterns.saga.common.grpc.Item toGrpcItem(Item item) {
        return net.patterns.saga.common.grpc.Item.newBuilder()
                .setName(item.getName())
                .setVendor(item.getVendor())
                .setVersion(item.getVersion())
                .setPrice(item.getPrice().longValue())
                .build();
    }
}
