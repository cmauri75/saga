package net.patterns.saga.inventoryservice.controller;

import lombok.AllArgsConstructor;
import net.patterns.saga.common.model.inventory.InventoryRequestDTO;
import net.patterns.saga.common.model.inventory.InventoryResponseDTO;
import net.patterns.saga.inventoryservice.entity.InventoryEvent;
import net.patterns.saga.inventoryservice.entity.Stock;
import net.patterns.saga.inventoryservice.service.InventoryService;
import net.patterns.saga.inventoryservice.support.StockNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory-service")
@AllArgsConstructor
public class InventoryController {
    private final InventoryService service;


    @PostMapping("/take")
    public InventoryResponseDTO take(@RequestBody final InventoryRequestDTO requestDTO) throws StockNotFoundException {
        return service.takeFromInventory(requestDTO);
    }

    @PostMapping("/put")
    public void put(@RequestBody final InventoryRequestDTO requestDTO) throws StockNotFoundException {
        service.addToInventory(requestDTO);
    }

    @GetMapping("/stock")
    public List<Stock> getStock() {
        return service.getStocks();
    }

    @GetMapping("/stock/{productId}")
    public Stock getStock(@PathVariable("productId") Integer productId) {
        return service.getStock(productId);
    }

    @GetMapping("/inventoryEvent")
    public List<InventoryEvent> getEvents() {
        return service.getEvents();
    }


}
