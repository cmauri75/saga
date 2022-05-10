# Saga pattern
Example implementation of saga pattern with "annessi&connessi". An incremental tutorial

## Scatter&Gather
Very usefull design patterns, it's applicable in the case you've many task that can be executed in parallel than data is collected and composite result is returned.

Main "Enterprise Integration Patterns for the distributed system architecture"
```mermaid
graph LR
A(User) --> B[Best Price Service - Aggreator] -->
    C[Publisher] -->  X1[Amazon]
    C --> X2[Ebay]
    C --> X3[Alibaba]
    X1 --> B
    X2 --> B
    X3 --> B
```

## Problem
We implement the pattern to solve a typical problem:

Microservices env:
* Order Service
* Payment Service
* Inventory Service

NB: Any of it has it's own DB. Order is complete if and only if both payment and inventory completes successfully, if one ot that fails both should rollback

Solutions:
* Orchestration approach: we've a separate service that orchestrate transaction, if everything is ok it marks order ok, otherwise it's cancelled.
* Choreography approach: problem is solved basing on event-sourcing. Events can be accepted or rejected in a centralized way so no extra service is needed. Take a look to [CQRS](https://github.com/cmauri75/cqrs)
