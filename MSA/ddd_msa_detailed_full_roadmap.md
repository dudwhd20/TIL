# DDD + MSA 설계 로드맵 (전체 이론 + 예제 포함)

---

## 1단계: 책임 중심 객체 설계

- 객체는 데이터를 담는 구조물이 아니라, **책임을 가진 주체**
- 메서드는 단순 getter/setter가 아니라, 의미 있는 행동이어야 함

```java
public class User {
    private int point;

    public void addPoint(int value) {
        this.point += value;
    }

    public boolean isVip() {
        return point >= 100;
    }
}
```

---

## 2단계: SRP (단일 책임 원칙)

- 클래스는 하나의 변경 이유만 가져야 함
- 역할이 섞이면 수정 시 위험 커짐

```java
// SRP 위반
public class ReportHandler {
    public String generate() {}
    public void print(String r) {}
    public void save(String r) {}
}

// SRP 준수
public class ReportGenerator { ... }
public class Printer { ... }
public class FileSaver { ... }
```

---

## 3단계: Entity vs Value Object

- **Entity**: 식별자(ID)가 있고 상태가 바뀜
- **VO**: 값 자체로 동일성을 판단. 불변 객체로 설계

```java
public class Address {
    private final String city;
    private final String street;
}
```

---

## 4단계: DIP (의존 역전 원칙)

- 구현체가 아니라 인터페이스에 의존해야 변경에 유연함

```java
public interface DiscountPolicy {
    int calculate(int amount);
}
```

---

## 5단계: OCP (개방-폐쇄 원칙)

- 기능 추가는 가능, 기존 코드 변경은 하지 말 것

```java
public class DiscountService {
    private Map<String, DiscountPolicy> policies;

    public int calculate(String grade, int amount) {
        return policies.get(grade).calculate(amount);
    }
}
```

---

## 6단계: LSP (리스코프 치환 원칙)

- 자식 클래스가 부모의 기능을 대체하지 못하면 상속을 잘못 쓴 것

---

## 7단계: ISP (인터페이스 분리 원칙)

- 하나의 큰 인터페이스보다 여러 개의 작은 인터페이스

```java
public interface Printable { void print(); }
public interface Scannable { void scan(); }
```

---

## 8단계: Bounded Context

- 하나의 도메인은 하나의 컨텍스트로 구분
- 유비쿼터스 언어는 해당 컨텍스트 내에서만 유효

---

## 9단계: Context Mapping

- 컨텍스트 간 관계를 명확히: ACL, Shared Kernel, Upstream/Downstream

---

## 10단계: 도메인 vs 애플리케이션 서비스

- 도메인 서비스: 핵심 비즈니스 규칙
- 애플리케이션 서비스: 유스케이스 흐름만 담당

```java
public class OrderAppService {
    public void placeOrder(...) {
        Order order = orderFactory.create(...);
        repository.save(order);
    }
}
```

---

## 11단계: Factory & Repository

- Factory는 객체 생성 책임
- Repository는 저장/조회 책임

---

## 12단계: Saga / Outbox 패턴

- Saga: 여러 서비스 간 작업 순서와 보상 흐름
- Outbox: DB 트랜잭션과 이벤트 발행 동기화

---

## 13단계: CQRS - Read Model 분리

- 쓰기 모델과 읽기 모델 분리 → 조회 최적화, 구조 단순화

---

## 14단계: 도메인 이벤트 활용

- 의미 있는 상태 변화 = 이벤트로 표현
- 다양한 후속 처리 (알림, 통계, 포인트 등) 가능

---

## 15단계: 패키지 구조 & REST 설계

- 도메인 기준으로 패키지 구성
- REST URI는 유비쿼터스 언어 기반

```text
/order
/order/{id}/cancel
```

---
