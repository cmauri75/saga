package net.patterns.saga.common.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchRequest {
    @NonNull
    private String item;
}
