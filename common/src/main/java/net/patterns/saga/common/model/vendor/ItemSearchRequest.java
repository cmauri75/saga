package net.patterns.saga.common.model.vendor;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchRequest {
    @NonNull
    private String item;
}
