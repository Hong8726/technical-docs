package com.hong.disclosure.user.infrastructure.event;

import com.hong.disclosure.user.infrastructure.EventPublisher;
import com.hong.disclosure.user.infrastructure.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @author 홍보람 (qhfka2854@gmail.com)
 */
@Component
class RabbitEventPublisher implements EventPublisher {
    private final Logger log;

    private final RabbitTemplate rabbitTemplate;

    RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.log = LoggerFactory.getLogger(RabbitEventPublisher.class);
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public CompletableFuture<Void> publish(String routingKey, EventType type, byte[] payload) {
        log.info("[publish] routingKey={}, type={}", routingKey, type.name());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("EVENT-TYPE", type.name());
        Message message = new Message(payload, messageProperties);

        // disclosure.exchange로 메시지 발행
        return CompletableFuture.completedFuture(message)
                .thenAccept(m -> rabbitTemplate.send("disclosure.exchange", routingKey, m))
                .thenAccept(v -> log.info("[publish] {}, {}, {} published.", routingKey, messageProperties, new String(payload)));
    }
}
