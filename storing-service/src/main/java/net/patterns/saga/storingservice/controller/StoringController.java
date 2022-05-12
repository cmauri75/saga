package net.patterns.saga.storingservice.controller;

import lombok.AllArgsConstructor;
import net.patterns.saga.common.model.vendor.Item;
import net.patterns.saga.common.model.storing.StoreCounter;
import net.patterns.saga.storingservice.service.StoringService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/storing-service")
@AllArgsConstructor
public class StoringController {
    private final StoringService storingService;

    @GetMapping("/store")
    public StoreCounter size() {
        return new StoreCounter(storingService.size());
    }

    @PostMapping("/store")
    public StoreCounter store(@RequestBody Item item) {
        return new StoreCounter(storingService.store(item));
    }
}
