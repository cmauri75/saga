package net.patterns.saga.storingservice.service;

import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.Item;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StoringService {

    private final List<Item> bests = new ArrayList<>();

    @PostConstruct
    public void init() {
        log.debug("Initialized storing service");
    }

    public int store(Item item) {
        bests.add(item);
        return this.size();
    }

    public int size() {
        return bests.size();
    }



}
