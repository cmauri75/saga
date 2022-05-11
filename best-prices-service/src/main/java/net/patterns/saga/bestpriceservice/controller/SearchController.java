package net.patterns.saga.bestpriceservice.controller;

import lombok.AllArgsConstructor;
import net.patterns.saga.bestpriceservice.service.ScatterGatherService;
import net.patterns.saga.common.model.ItemSearchRequest;
import net.patterns.saga.common.model.ItemSearchResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("item")
@AllArgsConstructor
public class SearchController {

    private ScatterGatherService service;

    @GetMapping("/async/{name}")
    public Mono<ItemSearchResponse> searchAsync(@PathVariable String name) {
        return this.service.broadcastAsync(ItemSearchRequest.builder().item(name).build());
    }

    @GetMapping("/sync/{name}")
    public ItemSearchResponse searchSync(@PathVariable String name) {
        return this.service.broadcastSync(ItemSearchRequest.builder().item(name).build());
    }
}
