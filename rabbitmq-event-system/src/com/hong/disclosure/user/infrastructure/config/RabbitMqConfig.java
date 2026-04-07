package com.hong.disclosure.user.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 설정 클래스.
 * Exchange를 선언하고 RabbitAdmin을 통해 자동으로 생성한다.
 *
 * <p>네이밍 컨벤션:
 * <ul>
 *   <li>Exchange: disclosure.exchange (도메인 레벨)</li>
 *   <li>Routing Key: disclosure.{entity}.events (예: disclosure.user.events)</li>
 *   <li>Queue: {consuming-service}.{domain}.event.queue (수신 서비스별 생성)</li>
 * </ul>
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
@Configuration
class RabbitMqConfig {

    /**
     * RabbitAdmin 빈 생성.
     * Exchange, Queue, Binding을 자동으로 선언하고 생성한다.
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * disclosure.exchange 선언.
     * Topic Exchange로 생성하여 라우팅 키 패턴 매칭을 지원한다.
     *
     * <p>발행되는 이벤트 예시:
     * <ul>
     *   <li>disclosure.user.created - 사용자 생성</li>
     *   <li>disclosure.user.updated - 사용자 수정</li>
     *   <li>disclosure.user.deleted - 사용자 삭제</li>
     * </ul>
     */
    @Bean
    public DirectExchange disclosureExchange() {
        return ExchangeBuilder
                .directExchange("disclosure.exchange")
                .durable(true)
                .build();
    }

    /**
     * user-management 서비스의 User 도메인 이벤트 수신용 Queue.
     * 네이밍 컨벤션: {service}.{domain}.event.queue
     * 보상 트랜잭션 이벤트를 수신한다.
     */
    @Bean
    public Queue userUserEventQueue() {
        return QueueBuilder
                .durable("user.user.event.queue")
                .build();
    }

    /**
     * user.user.event.queue와 disclosure Exchange 바인딩.
     * user.compensation.events 라우팅 키로 발행된 보상 이벤트를 수신한다.
     */
    @Bean
    public Binding userUserCompensationBinding() {
        return BindingBuilder
                .bind(userUserEventQueue())
                .to(disclosureExchange())
                .with("user.compensation.events");
    }
}
