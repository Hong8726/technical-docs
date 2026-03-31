# 클린 아키텍처 설계 문서

## 목차
1. [아키텍처 개요](#아키텍처-개요)
2. [레이어별 상세 설명](#레이어별-상세-설명)
3. [핵심 패턴](#핵심-패턴)
4. [데이터 흐름](#데이터-흐름)
5. [설계 결정 사항](#설계-결정-사항)

## 아키텍처 개요

이 프로젝트는 **클린 아키텍처(Clean Architecture)** 및 **헥사고날 아키텍처(Hexagonal Architecture)** 원칙을 따릅니다.

### 의존성 방향
```
Presentation → Application → Domain ← Infrastructure
     (Adapter)    (Use Case)   (Core)    (Adapter)
```

**핵심 원칙:**
- 도메인은 어디에도 의존하지 않음 (순수 비즈니스 로직)
- 모든 의존성은 안쪽(도메인)을 향함
- 외부 세계와의 통신은 Adapter를 통해서만

## 레이어별 상세 설명

### 1. Domain Layer (Core)

**역할:** 비즈니스 규칙과 로직의 핵심

**구성 요소:**
- **Entity:** 비즈니스 개념을 표현하는 객체
- **Value Object:** 불변 객체로 구현된 값
- **Enum:** 도메인 열거형
- **Converter:** 변환 전략 인터페이스

**특징:**
- 외부 프레임워크나 라이브러리에 의존하지 않음
- 순수 Java 코드로만 구성
- 비즈니스 규칙 검증 로직 포함

**예시:**
```java
// Domain Entity
public record DataEntity(String id, String name) {
    public void validate() {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must not be empty");
        }
    }
}

// Domain Converter Interface
public interface Converter<I, O> {
    O convert(I input);
}
```

### 2. Application Layer (Use Case)

**역할:** 애플리케이션의 비즈니스 흐름 조율

**구성 요소:**
- **Service Interface:** 유스케이스 정의
- **Service Implementation:** 비즈니스 흐름 구현

**특징:**
- 도메인 객체만 사용
- Infrastructure의 구체 구현을 알지 못함 (의존성 역전)
- 트랜잭션 경계 관리

**예시:**
```java
// Application Service Interface
public interface ExternalApiService {
    SearchCriteria searchData(SearchCriteria criteria);
    String getDetailData(String entityId, String resourceId);
}

// Implementation
@Service
class ExternalApiDomainService implements ExternalApiService {
    // Infrastructure 인터페이스에 의존
    private final DataSearchClient client;
    private final DataRepository repository;
    private final Converter<String, String> converter;

    @Override
    public String getDetailData(String entityId, String resourceId) {
        // 1. Repository(DB) 조회
        return repository.findById(entityId, resourceId)
            .orElseGet(() -> {
                // 2. API 호출
                String rawData = client.fetchDocument(resourceId);
                // 3. 변환
                String converted = converter.convert(rawData);
                // 4. Repository에 저장
                repository.save(entityId, resourceId, converted);
                return converted;
            });
    }
}
```

### 3. Infrastructure Layer (Adapter - Driven)

**역할:** 외부 시스템과의 통신

**구성 요소:**
- **Client:** 외부 API 클라이언트
- **Repository:** 데이터 저장소
- **Config:** 인프라 설정
- **Resource:** Infrastructure 전용 DTO
- **Exception:** 인프라 예외

**특징:**
- 외부 API, Database 등과 직접 통신
- Domain 인터페이스를 구현
- 기술 스택 상세 구현 포함

**예시:**
```java
// 추상 클라이언트 (Template Method Pattern)
public abstract class ExternalApiClient<T> {
    protected final RestTemplate restTemplate;
    protected final ApiProperties properties;

    protected void addAuthenticationKey(MultiValueMap<String, String> params) {
        params.add("api_key", properties.getApiKey());
    }

    protected <R> R callApi(String uri, Class<R> responseType) {
        return restTemplate.getForObject(uri, responseType);
    }

    // 하위 클래스가 구현
    protected abstract MultiValueMap<String, String> buildQueryParameters(T request);
}

// 구체 클라이언트
@Component
public class DataSearchClient extends ExternalApiClient<SearchRequest> {
    public SearchCriteria search(SearchCriteria criteria) {
        // Domain -> Infrastructure DTO
        MultiValueMap<String, String> params = buildQueryParameters(
            SearchRequest.from(criteria)
        );

        SearchResponse response = callApi(uri, SearchResponse.class);

        // Infrastructure DTO -> Domain
        return SearchResponse.toDomain(response);
    }

    @Override
    protected MultiValueMap<String, String> buildQueryParameters(SearchRequest request) {
        // 구체적인 파라미터 빌드 로직
    }
}
```

### 4. Presentation Layer (Adapter - Driving)

**역할:** 사용자 인터페이스 (REST API)

**구성 요소:**
- **Controller:** REST 엔드포인트
- **Resource:** Presentation 전용 DTO

**특징:**
- HTTP 요청/응답 처리
- DTO ↔ Domain 변환
- 입력값 검증

**예시:**
```java
@RestController
@RequestMapping("/api/v1/external")
public class ExternalApiController {
    private final ExternalApiService service;

    @GetMapping("/search")
    public SearchResponseDto search(
        @RequestHeader("Entity-Id") String entityId,
        @Valid SearchRequestDto request) {

        // Presentation DTO -> Domain
        SearchCriteria criteria = request.toDomain(entityId);

        // Application Service 호출
        SearchCriteria result = service.searchData(criteria);

        // Domain -> Presentation DTO
        return SearchResponseDto.fromDomain(result);
    }
}
```

## 핵심 패턴

### 1. 의존성 역전 원칙 (Dependency Inversion Principle)

**문제:** Application Layer가 Infrastructure의 구체 구현에 의존하면 변경에 취약

**해결:**
```java
// Application에서 인터페이스 정의
public interface DataRepository {
    Optional<String> findById(String entityId, String resourceId);
    void save(String entityId, String resourceId, String content);
}

// Infrastructure에서 구현
@Repository
public class InMemoryDataRepository implements DataRepository {
    private final Map<String, String> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<String> findById(String entityId, String resourceId) {
        return Optional.ofNullable(storage.get(generateKey(entityId, resourceId)));
    }
}
```

### 2. 전략 패턴 (Strategy Pattern)

**목적:** 데이터 변환 로직을 유연하게 교체

```java
// 전략 인터페이스
public interface Converter<I, O> {
    O convert(I input);
}

// 구체 전략 1
@Component
public class XmlStringConverter implements Converter<String, String> {
    public String convert(String input) {
        // XML 파싱 로직
    }
}

// 구체 전략 2
@Component
public class JsonConverter implements Converter<String, String> {
    public String convert(String input) {
        // JSON 파싱 로직
    }
}

// 사용
@Service
class Service {
    private final Converter<String, String> converter;

    // Spring이 적절한 구현체 주입
    public Service(Converter<String, String> converter) {
        this.converter = converter;
    }
}
```

### 3. 템플릿 메서드 패턴 (Template Method Pattern)

**목적:** 공통 로직을 추상 클래스로 추출하고 가변 부분만 하위 클래스가 구현

```java
public abstract class ExternalApiClient<T> {
    // 공통 로직
    protected void addAuthenticationKey(MultiValueMap<String, String> params) {
        params.add("api_key", properties.getApiKey());
    }

    protected <R> R callApi(String uri, Class<R> responseType) {
        return restTemplate.getForObject(uri, responseType);
    }

    // 가변 부분 - 하위 클래스가 구현
    protected abstract MultiValueMap<String, String> buildQueryParameters(T request);
}
```

### 4. Repository 패턴

**목적:** 데이터 접근 로직 추상화

```java
// 인터페이스로 추상화
public interface DataRepository {
    Optional<String> findById(String entityId, String resourceId);
    void save(String entityId, String resourceId, String content);
}

// 다양한 구현 가능
// - InMemoryDataRepository (개발/테스트)
// - RedisDataRepository (캐시)
// - DatabaseDataRepository (영구 저장)
// - FileSystemDataRepository (파일 저장)
```

### 5. Repository 우선 조회 패턴

**목적:** 중복 API 호출 방지 및 응답 시간 개선

**흐름:**
1. Repository(DB)에서 기존 데이터 조회
2. 데이터 있음 → 즉시 반환
3. 데이터 없음 → 외부 API 호출
4. API 응답을 변환하여 Repository(DB)에 저장
5. 결과 반환

```java
public String getDetailData(String entityId, String resourceId) {
    // 1. Repository(DB) 조회
    return repository.findById(entityId, resourceId)
        .orElseGet(() -> {
            // 2. API 호출
            String rawData = documentClient.fetchDocument(resourceId);

            // 3. 변환
            String converted = xmlStringConverter.convert(rawData);

            // 4. Repository에 저장
            repository.save(entityId, resourceId, converted);
            return converted;
        });
}
```

## 데이터 흐름

### 1. 요청 처리 흐름

```
Client
  ↓ HTTP Request
[Presentation Layer]
  ↓ SearchRequestDto
  ↓ .toDomain()
  ↓ SearchCriteria (Domain)
[Application Layer]
  ↓
[Infrastructure Layer - Client]
  ↓ SearchRequest.from(domain)
  ↓ SearchRequest (Infrastructure DTO)
  ↓ HTTP Call
External API
  ↓ SearchResponse (Infrastructure DTO)
  ↓ SearchResponse.toDomain()
  ↓ SearchCriteria (Domain)
[Application Layer]
  ↓
[Presentation Layer]
  ↓ SearchResponseDto.fromDomain()
  ↓ SearchResponseDto
  ↓ HTTP Response
Client
```

### 2. 레이어별 DTO 변환

**Presentation Layer:**
```java
// Request: Client → Domain
public SearchCriteria toDomain(String entityId) {
    return SearchCriteria.createForRequest(...);
}

// Response: Domain → Client
public static SearchResponseDto fromDomain(SearchCriteria criteria) {
    return new SearchResponseDto(...);
}
```

**Infrastructure Layer:**
```java
// Request: Domain → External API
public static SearchRequest from(SearchCriteria criteria) {
    return new SearchRequest(...);
}

// Response: External API → Domain
public static SearchCriteria toDomain(SearchResponse response) {
    return SearchCriteria.builder()...build();
}
```

## 설계 결정 사항

### 1. Record vs Class

**Record 사용:**
- DTO (불변 데이터 전송 객체)
- 간단한 도메인 엔티티

**Class 사용:**
- 복잡한 로직이 있는 도메인 객체
- 상태 변경이 필요한 경우

### 2. 예외 처리 전략

**도메인 계층:**
- 비즈니스 규칙 위반 → IllegalArgumentException
- 도메인 예외는 unchecked exception

**인프라 계층:**
- API 호출 실패 → ApiException (custom)
- Repository 접근 실패 → 로깅 후 fallback

**애플리케이션 계층:**
- 예외 전파 (필요시 변환)

### 3. 설정 관리

**민감 정보:**
- 환경변수로 주입 (${EXTERNAL_API_KEY})
- 절대 코드에 하드코딩하지 않음

**프로파일별 설정:**
- dev, staging, prod 환경별 분리
- application-{profile}.yml 사용

## 장점 및 트레이드오프

### 장점
1. **테스트 용이성:** 각 레이어를 독립적으로 테스트
2. **유지보수성:** 관심사의 명확한 분리
3. **확장성:** 새로운 기능 추가 시 기존 코드 수정 최소화
4. **비즈니스 로직 보호:** 도메인이 외부 변경에 영향받지 않음

### 트레이드오프
1. **초기 복잡도:** 단순한 CRUD에는 과도할 수 있음
2. **DTO 변환 오버헤드:** 레이어마다 DTO 변환 필요
3. **파일 수 증가:** 레이어별로 파일이 많아짐

### 적용 권장 사항
- 복잡한 비즈니스 로직이 있는 경우
- 장기적으로 유지보수가 필요한 프로젝트
- 여러 외부 시스템과 통합이 필요한 경우
- 테스트 커버리지가 중요한 경우

## 참고 자료
- Clean Architecture (Robert C. Martin)
- Hexagonal Architecture (Alistair Cockburn)
- Domain-Driven Design (Eric Evans)
