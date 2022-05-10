package net.patterns.saga.common.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Set;

@Data
@Builder
public class ItemSearchResponse {
    @NonNull
    private ItemSearchRequest itemSearchRequest;
    @NonNull
    private Set<Item> itemSet;
}
