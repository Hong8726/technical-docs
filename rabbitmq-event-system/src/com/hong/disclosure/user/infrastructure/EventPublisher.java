package com.hong.disclosure.user.infrastructure;


import java.util.concurrent.CompletableFuture;

/**
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public interface EventPublisher {
    CompletableFuture<Void> publish(String routingKey, EventType type, byte[] payload);
}
