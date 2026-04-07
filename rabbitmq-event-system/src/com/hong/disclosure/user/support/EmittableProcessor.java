package com.hong.disclosure.user.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hong.disclosure.user.infrastructure.EventPublisher;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author 홍보람 (qhfka2854@gmail.com)
 */
@Aspect
@Component
class EmittableProcessor {
    private final Logger log;

    private final EventPublisher eventPublisher;

    private final ObjectMapper objectMapper;

    EmittableProcessor(EventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.log = LoggerFactory.getLogger(EmittableProcessor.class);
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(emittable)")
    public Object execute(ProceedingJoinPoint joinPoint, Emittable emittable) throws Throwable {
        Object result = joinPoint.proceed();
        this.convert(result)
                .ifPresent(payload -> eventPublisher
                        .publish(emittable.routingKey(), emittable.type(), payload)
                        .thenAccept(v -> log.info("[execute] routingKey={}, type={} published.",
                                emittable.routingKey(), emittable.type()))
                        .exceptionally(ex -> {
                            log.error("[execute] Failed to publish event: routingKey={}, type={}",
                                    emittable.routingKey(), emittable.type(), ex);
                            return null;
                        }));
        return result;
    }

    private Optional<byte[]> convert(Object returningValue) {
        try {
            return Optional.of(objectMapper.writeValueAsBytes(returningValue));
        } catch (Exception e) {
            log.error("[convert] Failed to convert object to bytes: {}", returningValue, e);
            return Optional.empty();
        }
    }
}
