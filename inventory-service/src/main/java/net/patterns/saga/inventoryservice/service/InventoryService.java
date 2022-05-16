package net.patterns.saga.inventoryservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.inventory.InventoryRequestDTO;
import net.patterns.saga.common.model.inventory.InventoryResponseDTO;
import net.patterns.saga.common.model.inventory.InventoryStatus;
import net.patterns.saga.inventoryservice.entity.InventoryEvent;
import net.patterns.saga.inventoryservice.entity.Stock;
import net.patterns.saga.inventoryservice.repository.InventoryEventRepository;
import net.patterns.saga.inventoryservice.repository.StockRepository;
import net.patterns.saga.inventoryservice.support.DtoConverter;
import net.patterns.saga.inventoryservice.support.StockNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class InventoryService {

    StockRepository stockRepository;

    InventoryEventRepository inventoryEventRepository;

    @Transactional
    public InventoryResponseDTO takeFromInventory(final InventoryRequestDTO requestDTO) throws StockNotFoundException {
        log.info("Going to take from inventory: {}", requestDTO);
        Optional<Stock> oStock = stockRepository.findById(requestDTO.getProductId());
        if (oStock.isEmpty())
            throw new StockNotFoundException();

        Stock stock = oStock.get();

        InventoryResponseDTO responseDTO = DtoConverter.requestToResponse(requestDTO);

        if (stock.getQuantity() > 0) {
            responseDTO.setStatus(InventoryStatus.AVAILABLE);

            stock.setQuantity(stock.getQuantity() - 1);

            inventoryEventRepository.save(
                    InventoryEvent.builder()
                            .id(UUID.randomUUID())
                            .productId(requestDTO.getProductId())
                            .change(-1)
                            .build());
        } else responseDTO.setStatus(InventoryStatus.UNAVAILABLE);

        return responseDTO;
    }

    @Transactional
    public void addToInventory(final InventoryRequestDTO requestDTO) throws StockNotFoundException {
        log.info("Going to move repository: {}", requestDTO);

        Optional<Stock> oStock = stockRepository.findById(requestDTO.getProductId());
        if (oStock.isEmpty())
            throw new StockNotFoundException();
        Stock stock = oStock.get();

        stock.setQuantity(stock.getQuantity() + 1);

        inventoryEventRepository.save(
                InventoryEvent.builder()
                        .id(UUID.randomUUID())
                        .productId(requestDTO.getProductId())
                        .change(1)
                        .build());
    }


    public List<Stock> getStocks() {
        return stockRepository.findAll();
    }

    public Stock getStock(Integer productID) {
        return stockRepository.findById(productID)
                .orElse(Stock.builder()
                        .productId(productID)
                        .quantity(-1)
                        .build());
    }

    public List<InventoryEvent> getEvents() {
        return inventoryEventRepository.findAll();
    }
}
