package net.patterns.saga.common.util;

public class Constants {
    private Constants(){}
    public static final String ORCHESTRATOR_NATS_ORDER_SUBJECT = "order.create";
    public static final String ORDER_QUEUE_NAME = "order_queue_name";

    public static final String CHOREOGRAPHY_NATS_ORDER_EVENTS = "chore.orders";

    public static final String PAYMENTS_QUEUE_NAME = "payments_queue_name";
    public static final String INVENTORY_QUEUE_NAME = "inventory_queue_name";


}
