package net.patterns.saga.common.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigInteger;

@Data
@Builder
public class Item {
    @NonNull
    private String name;
    @NonNull
    private String version;
    @NonNull
    private String vendor;
    @NonNull
    private BigInteger price;
}
