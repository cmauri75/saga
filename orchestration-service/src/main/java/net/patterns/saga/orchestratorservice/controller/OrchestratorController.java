package net.patterns.saga.orchestratorservice.controller;

import lombok.AllArgsConstructor;
import net.patterns.saga.orchestratorservice.service.OrchestratorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory-service")
@AllArgsConstructor
public class OrchestratorController {
    private final OrchestratorService service;


    @GetMapping("/stock/{productId}")
    public void orchestrate(@PathVariable("productId") Integer productId) {
        service.toString();
    }




}
