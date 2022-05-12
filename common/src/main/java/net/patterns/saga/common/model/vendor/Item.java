package net.patterns.saga.common.model.vendor;

import lombok.*;

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
