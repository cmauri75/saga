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

    @GetMapping("/{name}")
    public Mono<ItemSearchResponse> search(@PathVariable String name) {
        return this.service.broadcast(ItemSearchRequest.builder().item(name).build());
    }

}
