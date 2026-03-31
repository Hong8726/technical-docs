# Log Appender RabbitMQ

분산 추적을 지원하는 RabbitMQ 기반 Logback Appender 라이브러리

## 개요

마이크로서비스 환경에서 서비스 간 로그를 수집하고 추적할 수 있도록 설계된 커스텀 Logback Appender입니다.
Vector + Loki + Grafana 기반 중앙 로깅 시스템과 함께 사용하여 분산 환경에서의 로그 분석을 용이하게 합니다.

## 주요 기능

### 1. Nexus 라이브러리 배포
- **배포 위치**: Nexus Maven Repository
- **현재 버전**: `0.5.RELEASE`
- 다른 서비스에서 Gradle/Maven 의존성으로 간편하게 추가 가능

### 2. Micrometer 기반 분산 추적 (Distributed Tracing)
- **자동 TraceId 전파**: Micrometer Tracing을 활용하여 서비스 간 요청 추적
- **Brave 브리지 사용**: `micrometer-tracing-bridge-brave`로 Zipkin 호환 추적
- 서비스 A → 서비스 B → 서비스 C 호출 시 동일한 `traceId`로 전체 흐름 추적 가능
- Grafana에서 traceId 기반 로그 필터링으로 전체 요청 체인 분석 가능

### 3. 로그 메시지 최적화
로그 전송 시 **필요한 필드만 추출**하여 네트워크 대역폭 및 저장 공간 절약:

```java
record LogMessage(
    String traceId,      // 분산 추적 ID
    String logger,       // 로거 이름
    String level,        // 로그 레벨 (INFO, WARN, ERROR 등)
    String service,      // 서비스명
    String message,      // 로그 메시지
    String timestamp     // ISO-8601 타임스탬프
)
```

**최적화 효과**:
- 불필요한 메타데이터 제외 (스택 트레이스, MDC, 스레드 정보 등)
- Record 타입으로 불변 객체 생성 → 메모리 효율성 향상
- JSON 직렬화 크기 최소화

## 아키텍처

```
[마이크로서비스들]
  ↓ (Logback + LogRabbitAppender)
[RabbitMQ]
  ↓
[Loki]
  ↓
[Grafana 대시보드]
```

## 기술 스택

- **Java 21** with Records
- **Spring Boot 3.5.4**
- **Logback** (SLF4J 구현체)
- **RabbitMQ** (Spring AMQP)
- **Micrometer Tracing** (Brave 브리지)
- **Loki** (로그 집계)
- **Grafana** (시각화)

## 사용 방법

### 1. 의존성 추가

**Gradle**:
```gradle
dependencies {
    implementation 'log-appender-rabbit-portfolio:1.0.0.RELEASE'
}

repositories {
    maven {
        url = 'http://domain-url/repository/maven-releases/'
    }
}
```

### 2. 설정 파일 (application.yml)

```yaml
spring:
  application:
    name: your-service-name  # 서비스 식별자

  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    log:
      exchange-name: { exchange.name }
      routing-key: { routing.key }
    template:
      observation-enabled: true
    listener:
      simple:
        acknowledge-mode: manual
        observation-enabled: true
```

### 3. 메인 애플리케이션 설정

라이브러리의 컴포넌트를 스캔하기 위해 메인 애플리케이션 클래스에 `@ComponentScan` 추가:

```java
@SpringBootApplication
@ComponentScan({"프로젝트 상위 패키지"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```


### 4. 자동 설정
라이브러리를 추가하면 `LogRabbitAppender`가 자동으로 Spring Bean으로 등록되고,
`프로젝트` 패키지 이하의 모든 로그를 자동으로 RabbitMQ로 전송합니다.

## 핵심 구성 요소

### LogRabbitAppender
- Logback의 `AppenderBase`를 확장
- `@PostConstruct`에서 자동으로 Logback에 등록
- 비동기 방식(`CompletableFuture`)으로 RabbitMQ 전송
- Micrometer Tracer에서 현재 Span의 traceId 자동 추출

### LogMessage
- Java Record 타입으로 불변 로그 메시지 표현
- Builder 패턴으로 생성
- Jackson으로 JSON 직렬화하여 RabbitMQ 전송

## 모니터링 및 추적

### Grafana에서 로그 검색
```logql
{service="your-service-name"} |= "ERROR"
{service="your-service-name"} | json | traceId="abc123def456"
```

### TraceId 기반 전체 요청 추적
1. Grafana Explore에서 특정 요청의 로그 확인
2. `traceId` 필드 복사
3. 필터: `| json | traceId="복사한ID"`
4. 모든 서비스에서 동일한 traceId를 가진 로그 확인 → 전체 요청 흐름 파악

## 개발 정보

- **Author**: 홍보람 
- **Version**: 0.5.RELEASE
- **License**: Proprietary

## 배포

```bash
# Gradle 빌드 및 Nexus 배포
./gradlew clean build publish
```

환경 변수 또는 `gradle.properties`에 Nexus 인증 정보 설정 필요:
```properties
NEXUS_USERNAME=your-username
NEXUS_PASSWORD=your-password
```
