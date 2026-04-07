
# 🔔 RabbitMQ 이벤트 발행·구독 공통 로직 사용 가이드

본 문서는 이벤트 기반 MSA 아키텍처에서 **공통 RabbitMQ 메시지 발행/구독 로직의 사용 방법**을 설명합니다.  
아래 가이드를 따르면 서비스 간 이벤트 연동을 쉽게 구축할 수 있습니다.

---

# 📌 전체 구조 요약

```
[서비스 A] --- (EventPublisher + @Emittable) ---> RabbitMQ
                                          |  
                                          v
                                 exchange.name
                                          |
                                          v
      Queue: {service}.{domain}.event.queue  --- @RabbitListener ---> [서비스 B]
                                                                 |  
                                                                 v
                                                      RabbitEventListener<T> 
                                                               |
                          성공 처리: ACK                        |                       실패 처리: 보상 + DLQ
                                    <--------------------------+---------------------------------------------→ DLQ
```

---

# 1. 📤 이벤트 발행 이벤트(Producer) — @Emittable + EventPublisher

## 1.1 이벤트 발행은 @Emittable 애노테이션으로 선언

메서드에 `@Emittable`을 붙이면, **메서드 실행 후 자동으로 RabbitMQ 이벤트 발행**이 이루어집니다.

```java
@Emittable(routingKey = "user.authority.events", type = EventType.DELETED)
public User remove(String id) { ... }
```

### ✔ 동작 방식
- AOP `EmittableProcessor`가 메서드를 가로채고
- 리턴 객체를 JSON 변환 후
- `EventPublisher.publish()`를 호출하여 RabbitMQ에 발행합니다.

### ✔ 발행 메시지 구조

헤더:

| Key         | Value |
|-------------|--------|
| EVENT-TYPE  | CREATED / UPDATED / DELETED |

본문: JSON 직렬화된 payload

---

# 2. 📥 이벤트 수신(Consumer) — RabbitEventListener<T>

수신 서비스는 공통 추상 클래스 **RabbitEventListener<T>** 를 상속해서 구현합니다.

```java
public class CredentialEventListener extends RabbitEventListener<UserCredentials>
```

---

# 2.1 Queue와 Listener 선언

```java
@RabbitListener(queues = "queue.name")
public void handleEvent(Message message, Channel channel) {
    super.handleEvent(message, channel);
}
```

---

# 2.2 이벤트 처리 흐름

```
1. 메시지 수신
2. EVENT-TYPE 헤더 추출
3. body JSON → 객체 역직렬화
4. handlerMap 에 등록된 핸들러 실행
5. ACK
6. 실패 시 보상 이벤트 발행 + DLQ 이동
```

---

# 2.3 handlerMap 설정

```java
this.handlerMap.put(EventType.DELETED, this::handleCredentialDeleted);
this.handlerMap.put(EventType.UPDATED, this::handleCredentialUpdated);
```

---

# 2.4 이벤트 처리 구현 예시

```java
@Override
protected void processEvent(EventType eventType, byte[] body, Channel channel, long deliveryTag) {
    try {
        UserCredentials credentials = deserialize(body, UserCredentials.class);

        getHandler(eventType).ifPresent(handler -> handler.accept(credentials));
        channel.basicAck(deliveryTag, false);
    } catch (Exception e) {
        handleFailure(channel, deliveryTag, "user.compensation.events", eventType, body);
    }
}
```

---

# 3. ⚠ 실패 처리 및 보상 트랜잭션

회피할 수 없는 예외 발생 시 다음 프로세스가 자동 실행됩니다.

```
1) 보상 이벤트 발행
2) channel.basicNack(… requeue = false)
3) DLQ 로 메시지 이동
```

### 보상 이벤트 발행 메서드

```java
publishCompensation("{domain}.compensation.events", eventType, body);
```

---

# 4. 📦 RabbitMQ 구성 규칙

## 4.1 Exchange (공통)

```
{domain}.exchange
```

---

## 4.2 Routing Key 규칙

```
{domain}.{entity}.events
예:
user.authority.events
form.template.events
report.document.events
```

---

## 4.3 Queue 규칙

```
{consuming-service}.{domain}.event.queue
예:
identity.credential.event.queue
report.document.event.queue
```

---

# 5. 🧱 설정 예시 (RabbitMqConfig)

```java
@Bean
public DirectExchange disclosureExchange() {
    return new DirectExchange("disclosure.exchange", true, false);
}

@Bean
public Queue identityEventQueue() {
    return new Queue("identity.credential.event.queue", true);
}

@Bean
public Binding userEventBinding() {
    return BindingBuilder.bind(identityEventQueue())
            .to(disclosureExchange())
            .with("user.authority.events");
}
```

---

# 6. 🔧 개발자가 해야 할 일 요약

## ✔ 이벤트 발행(Service → MQ)
1. `@Emittable(routingKey, type)` 붙인다.
2. 리턴 객체가 곧 MESSAGE payload가 됨.

## ✔ 이벤트 수신(MQ → 서비스)
수신 서비스는 반드시 RabbitMQ 설정을 직접 추가해야 함.

1. Queue 생성
```
   @Bean
   public Queue identityCredentialEventQueue() {
   return new Queue("identity.credential.event.queue", true);
   }
```

2. Binding 등록
```
   @Bean
   public Binding bindIdentityCredentialEvent() {
   return BindingBuilder.bind(identityCredentialEventQueue())
   .to(disclosureExchange())
   .with("user.authority.events");
   }
```
3. Listener 구현
```
1) RabbitEventListener<T> 상속
2) handlerMap 에 이벤트 타입별 처리기 등록
3) @RabbitListener 로 Queue 연결
```





---

# 7. 📚 CredentialEventListener 예시 흐름

```
Identity remove() → @Emittable → MQ 발행
 → identity.credential.event.queue 로 라우팅
 → CredentialEventListener.handleEvent()
 → handleCredentialDeleted()
 → ACK
```

---

# 8. 📝 결론

공통 로직을 사용하면:

- 이벤트 발행/수신 표준화
- 보상 트랜잭션 + DLQ 기반 안전한 장애 처리
- 비즈니스 로직만 구현하면 됨

