# MSA 실전 설계 이론 정리

---

## 1. 헥사고날 아키텍처 (Hexagonal Architecture)

### 핵심 개념

- 비즈니스 도메인 핵심 로직을 중심으로 모든 의존성을 바깥으로 밀어낸다.
- "포트와 어댑터" 구조
- 유지보수성 / 테스트 용이성 / 결합도 낮춤
- 모놀로식은 계층(layered) 중심 헥사고날은 의존성 방향 중심 (inside-out)

### 계층 구성

| 레이어 | 역할 |
|---|---|
| 도메인 (Domain) | 비즈니스 핵심 규칙 (엔티티, 도메인 서비스, 값 객체) |
| 어플리케이션 (Application) | 유스케이스 실행, 도메인 조합, 서비스 조합 |
| 포트 (Ports) | 어플리케이션 입출력 인터페이스 정의 (inbound, outbound) |
| 어댑터 (Adapters) | 실제 구현체 (DB, REST API, 메시지브로커 등) |

---

## 2. DDD 기반 설계

### 핵심 개념

- 복잡한 도메인 문제를 도메인 모델 중심으로 해결
- 애그리거트(Aggregate), 엔티티(Entity), 값 객체(Value Object), 도메인 이벤트 적극 활용
- 유비쿼터스 언어를 코드에 반영

### 핵심 구성 요소

| 구성 | 설명 |
|---|---|
| 애그리거트 | 비즈니스 단위 응집체 (ex. 주문, 상품 등) |
| 엔티티 | 식별자를 가진 객체 (ex. Product, Order) |
| 값 객체 | 식별자 없이 값 자체로 동등성 결정 (ex. Money, Address) |
| 도메인 이벤트 | 도메인 상태 변화 발생 시 발행 (ex. OrderCancelledEvent) |
| 유스케이스 서비스 | Application Service에서 유스케이스를 조합 |

---

## 3. Outbox + CDC 기반 Event-Driven 아키텍처

### 왜 Outbox 패턴?

- MSA간 이벤트 발행시 DB 트랜잭션과 메시지 발행의 일관성 문제 해결
- Local Transaction -> Outbox 테이블 기록 -> 이후 CDC로 메시지 발행

### Outbox 패턴 흐름

1. 업무 트랜잭션 내에서 Outbox 테이블에 이벤트 기록 (DB Commit)
2. Debezium (CDC) 이 DB의 Outbox 테이블을 모니터링
3. Kafka로 변경 이벤트 발행
4. 다른 서비스들은 Kafka Consumer로 이벤트 구독 및 비동기 처리

### Debezium 기반 CDC 장점

- 데이터 일관성 보장
- 기존 DB 트랜잭션 활용 가능
- 장애시 복구 및 재처리 용이

---

## 4. CDC 설계 주의사항

| 이슈 | 실전 해결 패턴 |
|---|---|
| JSON 타입 매핑 | DB: JSONB / JPA: Converter or JsonType 사용 |
| Debezium payload 이중 직렬화 | payload는 문자열로 저장하되 DB JSON 컬럼으로 |
| Binary handling mode | Debezium 커넥터에 `binary.handling.mode: base64` 필수 |
| Jackson 역직렬화 | DTO에 기본 생성자 + getter/setter 필수 |

---

