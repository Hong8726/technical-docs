package com.hong.disclosure.user.infrastructure;


import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

/**
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public interface EventListener {
    /**
     * RabbitMQ 메시지를 수신하여 처리한다.
     *
     * <p>하위 클래스는 {@code @RabbitListener} 어노테이션으로 이 메서드를 오버라이드하여
     * Queue를 지정해야 한다.
     *
     * @param message RabbitMQ 메시지
     */
    void handleEvent(Message message, Channel channel);
}
