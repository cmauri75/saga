package net.patterns.saga.common.model.vendor;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Builder
public class ItemSearchResponse {
    @NonNull
    private ItemSearchRequest itemSearchRequest;
    @NonNull
    private List<Item> itemList;
}
