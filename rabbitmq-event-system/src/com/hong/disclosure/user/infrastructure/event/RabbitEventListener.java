package com.hong.disclosure.user.infrastructure.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.hong.disclosure.user.domain.User;
import com.hong.disclosure.user.infrastructure.EventListener;
import com.hong.disclosure.user.infrastructure.EventPublisher;
import com.hong.disclosure.user.infrastructure.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 이벤트 핸들러 추상 클래스.
 * RabbitMQ 메시지 수신 및 처리를 위한 공통 로직을 제공한다.
 *
 * <p>하위 클래스는 다음을 구현해야 한다:
 * <ul>
 *   <li>{@link #processEvent(EventType, byte[], Channel channel, long deliveryTag)} - 이벤트 타입별 처리 로직</li>
 *   <li>{@code @RabbitListener} 어노테이션으로 Queue 지정</li>
 * </ul>
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public abstract class RabbitEventListener implements EventListener {
    protected final Logger log;
    protected final ObjectMapper objectMapper;
    private final EventPublisher eventPublisher;
    protected final Map<EventType, Consumer<User>> handlerMap;

    protected RabbitEventListener(ObjectMapper objectMapper, EventPublisher eventPublisher) {
        this.log = LoggerFactory.getLogger(getClass());
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
        this.handlerMap = new EnumMap<>(EventType.class);
    }

    /**
     * RabbitMQ 메시지를 수신하여 처리한다.
     * EVENT-TYPE 헤더를 추출하고, 하위 클래스의 {@link #processEvent} 메서드를 호출한다.
     *
     * <p>하위 클래스는 {@code @RabbitListener} 어노테이션으로 이 메서드를 오버라이드하여
     * Queue를 지정해야 한다.
     *
     * @param message RabbitMQ 메시지
     * @throws ListenerExecutionFailedException 이벤트 처리 실패 시
     */
    @Override
    public void handleEvent(Message message, Channel channel) {
        EventType eventType = extractEventType(message);
        log.info("[handleEvent] type={}", eventType);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        processEvent(eventType, message.getBody(), channel, deliveryTag);
    }

    /**
     * 이벤트 타입별 처리 로직을 구현한다.
     *
     * @param eventType 이벤트 타입
     * @param body 메시지 본문 (JSON 바이트 배열)
     */
    protected abstract void processEvent(EventType eventType, byte[] body, Channel channel, long deliveryTag);

    /**
     * 메시지에서 EVENT-TYPE 헤더를 추출한다.
     *
     * @param message RabbitMQ 메시지
     * @return 이벤트 타입, 헤더가 없으면 null
     */
    protected EventType extractEventType(Message message) {
        String eventTypeStr = (String) message.getMessageProperties()
                .getHeaders().get("EVENT-TYPE");

        if (eventTypeStr == null) {
            log.warn("[extractEventType] EVENT-TYPE header is missing");
            return null;
        }

        try {
            return EventType.valueOf(eventTypeStr);
        } catch (IllegalArgumentException e) {
            log.warn("[extractEventType] Unknown EVENT-TYPE: {}", eventTypeStr);
            return null;
        }
    }

    /**
     * JSON 바이트 배열을 객체로 역직렬화한다.
     *
     * @param body JSON 바이트 배열
     * @param clazz 대상 클래스
     * @param <T> 타입 파라미터
     * @return 역직렬화된 객체
     * @throws IOException 역직렬화 실패 시
     */
    protected <T> T deserialize(byte[] body, Class<T> clazz) throws IOException {
        return objectMapper.readValue(body, clazz);
    }

    /**
     * 보상 이벤트를 발행한다.
     * 이벤트 처리 실패 시 다른 서비스에 보상 트랜잭션을 요청하기 위해 사용한다.
     *
     * @param routingKey 보상 이벤트 라우팅 키 (예: "user.compensation.events")
     * @param eventType 이벤트 타입
     * @param payload 이벤트 페이로드 (JSON 바이트 배열)
     */
    protected void publishCompensation(String routingKey, EventType eventType, byte[] payload) {
        try {
            eventPublisher.publish(routingKey, eventType, payload)
                    .thenAccept(v -> log.info("[publishCompensation] routingKey={}, type={} published",
                            routingKey, eventType))
                    .exceptionally(ex -> {
                        log.error("[publishCompensation] Failed to publish: routingKey={}, type={}",
                                routingKey, eventType, ex);
                        return null;
                    });
        } catch (Exception e) {
            log.error("[publishCompensation] Exception while publishing: routingKey={}, type={}",
                    routingKey, eventType, e);
        }
    }

    /**
     * 이벤트 처리 실패 처리
     * - 실패에 대해 보상 이벤트를 발행하고 DLQ로 이동
     * - 무한 재시도를 방지하고 데이터 일관성을 유지
     *
     * @param channel RabbitMQ 채널
     * @param deliveryTag 메시지 배달 태그
     * @param compensationRoutingKey 보상 이벤트 라우팅 키
     * @param eventType 이벤트 타입
     * @param body 메시지 본문
     */
    protected void handleFailure(Channel channel, long deliveryTag,
                                 String compensationRoutingKey, EventType eventType, byte[] body) {
        try {
            // 보상 이벤트 발행 - 다른 서비스에 실패 알림
            publishCompensation(compensationRoutingKey, eventType, body);

            // 메시지를 Dead Letter Queue로 보냄 (재시도 하지 않음)
            channel.basicNack(deliveryTag, false, false);
            log.info("이벤트 처리 실패 - 보상 이벤트 발행 및 DLQ로 이동: deliveryTag={}", deliveryTag);
        } catch (IOException nackException) {
            log.error("NACK 실패: deliveryTag={}", deliveryTag, nackException);
        }
    }
}
