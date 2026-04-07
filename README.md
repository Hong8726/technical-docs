# Technical Deep-Dive: 아키텍처 및 공통 모듈 설계

본 저장소는 서비스의 지속 가능성과 확장성을 고려하여 설계한 핵심 모듈의 **설계 문서와 구현 코드**를 포함합니다.

---

## 1. 클린 & 헥사고날 아키텍처 설계 — DART Open API

의존성 역전 원칙(DIP)을 적용한 레이어 분리와 외부 API 통합 설계

- **설계 문서**: [`dart-open-api/README.md`](dart-open-api/README.md)
- **소스 코드**: [`dart-open-api/src/`](dart-open-api/src/)

| 패턴 | 적용 위치 |
|---|---|
| Clean Architecture | `application/` → `domain/` ← `infrastructure/` |
| Builder | `DartMainInfo.Builder` |
| Template Method | `DartClient<T>` → `DartMainInfoClient` |
| Strategy | `Converter<I, O>` → `HtmlConverter` |
| Cache-aside | `DartMainInfoDomainService.getMainInfo()` |

---

## 2. RabbitMQ 기반 이벤트 통신 시스템

AOP 기반 메시지 발행 자동화와 Saga 보상 트랜잭션 설계

- **설계 문서**: [`rabbitmq-event-system/README.md`](rabbitmq-event-system/README.md)
- **소스 코드**: [`rabbitmq-event-system/src/`](rabbitmq-event-system/src/)

| 패턴 | 적용 위치 |
|---|---|
| AOP + Custom Annotation | `@Emittable` + `EmittableProcessor` |
| Template Method | `RabbitEventListener` → `UserEventListener` |
| Saga Compensation | `handleFailure()` → `publishCompensation()` → DLQ |
| Strategy (Interface) | `EventPublisher`, `EventListener` |

---

## 3. 분산 로깅 및 추적 시스템

커스텀 Logback Appender와 Micrometer 기반 TraceID 전파

- **설계 문서**: [`log-appender-rabbit/README.md`](log-appender-rabbit/README.md)
- **소스 코드**: [`log-appender-rabbit/src/`](log-appender-rabbit/src/)

| 패턴 | 적용 위치 |
|---|---|
| Logback Extension | `LogRabbitAppender extends AppenderBase` |
| Builder | `LogMessage.Builder` |
| Lifecycle Management | `@PostConstruct` / `@PreDestroy` |
| Distributed Tracing | Micrometer `Tracer` → TraceID 추출 |
