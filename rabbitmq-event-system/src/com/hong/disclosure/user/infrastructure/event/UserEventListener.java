package com.hong.disclosure.user.infrastructure.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.hong.disclosure.user.domain.User;
import com.hong.disclosure.user.infrastructure.EventPublisher;
import com.hong.disclosure.user.infrastructure.EventType;
import com.hong.disclosure.user.infrastructure.UserRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * 사용자 이벤트 핸들러.
 * RabbitMQ로부터 사용자 관련 이벤트를 수신하여 처리한다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>보상 트랜잭션 처리 (COMPENSATION)</li>
 *   <li>향후 필요 시 다른 이벤트 타입 처리 확장 가능</li>
 * </ul>
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
@Component
class UserEventListener extends RabbitEventListener {
    private final UserRepository userRepository;

    UserEventListener(ObjectMapper objectMapper, EventPublisher eventPublisher, UserRepository userRepository) {
        super(objectMapper, eventPublisher);
        this.userRepository = userRepository;
        this.handlerMap.put(EventType.CREATED, this::handleCreateCompensation);
        this.handlerMap.put(EventType.DELETED, this::handleDeleteCompensation);
    }

    /**
     * user.user.event.queue로부터 이벤트를 수신하여 처리한다.
     * {service}.{domain}.event.queue
     * @param message RabbitMQ 메시지
     */
    @Override
    @RabbitListener(queues = "user.user.event.queue")
    public void handleEvent(Message message, Channel channel) {
        super.handleEvent(message, channel);
    }

    /**
     * 이벤트 타입별 처리 로직.
     *
     * @param eventType 이벤트 타입
     * @param body 메시지 본문
     */
    @Override
    @Transactional
    protected void processEvent(EventType eventType, byte[] body, Channel channel, long deliveryTag) {
        log.info("[processEvent] event: {}", eventType);
        try {
            User user = deserialize(body, User.class);
            this.getHandler(eventType)
                    .ifPresent(handler -> handler.accept(user));
            channel.basicAck(deliveryTag, false);
            log.info("사용자 이벤트 처리 완료: deliveryTag={}", deliveryTag);
        } catch (Exception e) {
            log.error("[processEvent] 이벤트 처리 실패: eventType={}, deliveryTag={}", eventType, deliveryTag, e);
            handleFailure(channel, deliveryTag, "user.compensation.events", eventType, body);
        }
    }

     private Optional<Consumer<User>> getHandler(EventType eventType) {
        return Optional.of(this.handlerMap.get(eventType));
     }

    /**
     * 보상 트랜잭션을 처리한다.
     * 다른 서비스에서 사용자 생성 처리 실패 시 사용자 상태를 복구한다.
     *
     * @param user 사용자 객체 (User JSON)
     */
    protected void handleCreateCompensation(User user) {
        log.warn("[handleCreateCompensation] userId={}, username={}", user.getId(), user.getUsername());

        userRepository.deleteById(user.getId().toString());
    }

    /**
     * 보상 트랜잭션을 처리한다.
     * 다른 서비스에서 사용자 삭제 처리 실패 시 사용자 상태를 복구한다.
     *
     * @param user 사용자 객체 (User JSON)
     */
    protected void handleDeleteCompensation(User user) {
        log.warn("[COMPENSATION] userId={}, username={}", user.getId(), user.getUsername());
        String originalUsername = user.getUsername().substring(0, user.getUsername().indexOf("_deleted_"));

        userRepository.findById(user.getId().toString())
                .filter(u -> !u.isEnabled())
                .ifPresentOrElse(
                        u -> {
                            u.enable();
                            u.setUsername(originalUsername);
                            userRepository.save(u);
                            log.info("[COMPENSATION] User restored: {}", user.getUsername());
                        },
                        () -> log.info("[COMPENSATION] User {} is already enabled or not found, skipping", user.getUsername())
                );
    }
}
