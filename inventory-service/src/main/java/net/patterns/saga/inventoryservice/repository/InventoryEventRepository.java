package net.patterns.saga.inventoryservice.repository;

import net.patterns.saga.inventoryservice.entity.InventoryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InventoryEventRepository extends JpaRepository<InventoryEvent, UUID> {
}
