package com.logappender.rabbit;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class LogRabbitAppender extends AppenderBase<ILoggingEvent> {

    private final RabbitTemplate rabbitTemplate;
    private final Tracer tracer;

    private final String serviceName;
    private final String loggerPackage;

    public LogRabbitAppender(RabbitTemplate rabbitTemplate, Tracer tracer,
                             @Value("${spring.application.name}") String serviceName,
                             @Value("${spring.rabbitmq.log.exchange-name}") String exchangeName,
                             @Value("${spring.rabbitmq.log.routing-key}") String routingKey,
                             @Value("${logging.appender.package:}") String loggerPackage) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setExchange(exchangeName);
        this.rabbitTemplate.setRoutingKey(routingKey);
        this.rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        this.tracer = tracer;
        this.serviceName = serviceName;
        this.loggerPackage = loggerPackage;
    }

    /**
     * Spring Bean 초기화 후 Logback에 Appender 등록
     */
    @PostConstruct
    void init() {
        LoggerContext logContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        this.setContext(logContext);
        this.setName("LOG_COLLECTOR");
        this.start();

        if (loggerPackage.isBlank()) {
            logContext.getLogger("ROOT").addAppender(this);
        } else {
            logContext.getLogger(loggerPackage).addAppender(this);
        }
    }

    /**
     * 로그 이벤트를 RabbitMQ로 전송
     */
    @Override
    protected void append(ILoggingEvent event) {

        LogMessage logMessage = LogMessage.builder(event)
                .traceId(getTraceId())
                .service(serviceName)
                .level(event.getLevel().toString())
                .timestamp(Instant.ofEpochMilli(event.getTimeStamp()).toString())
                .build();

        CompletableFuture.completedFuture(logMessage)
                .thenAccept(rabbitTemplate::convertAndSend);
    }

    /**
     * Micrometer Tracer에서 자동으로 트레이싱 정보 추출
     */
    private String getTraceId() {
        Span currentSpan = tracer.currentSpan();
        return Optional.ofNullable(currentSpan)
                .map(span -> {
                    TraceContext context = span.context();
                    return context.traceId();
                })
                .orElse("");
    }

    /**
     * Spring Bean 종료 시 Appender 정리
     */
    @PreDestroy
    void tearDown() {
        this.stop();
    }
}
