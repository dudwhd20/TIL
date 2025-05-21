
# 🪂 Kafka Dead Letter Topic(DLT) 실습 및 커스터마이징

## ✅ 목적
Kafka 리스너에서 메시지 소비 중 오류 발생 시 해당 메시지를 Dead Letter Topic(DLT)으로 자동 분기하여 유실을 방지하고, 후속 처리를 가능하게 한다.

---

## 📦 기본 DLT 처리 흐름

1. Kafka 리스너에서 메시지 처리 중 예외 발생
2. Spring Kafka의 `DefaultErrorHandler`가 예외 감지
3. `DeadLetterPublishingRecoverer`를 통해 `.DLT` 토픽으로 전송
4. 해당 DLT 토픽에서 후속 처리 가능

---

## 🧾 예제 DTO

```java
public class Order {
    public String orderId;
    public int quantity;
}
```

---

## ⚙️ application.yml 설정 예시

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: order-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

---

## 🛠️ 기본 리스너 및 예외 예시

```java
@KafkaListener(topics = "order-topic", groupId = "order-group")
public void handleOrder(Order order) {
    System.out.println("받은 주문: " + order.orderId);

    if ("error".equals(order.orderId)) {
        throw new RuntimeException("강제 예외 발생!");
    }
}
```

---

## ✅ 기본 DLT 구성

```java
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
    return new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 0)); // 재시도 없이 즉시 DLT 전송
}
```

### ➕ 자동 생성되는 DLT 토픽 명
```
<원래-토픽명>.<group-id>.DLT
예: order-topic.order-group.DLT
```

---

## 🛠️ DLT 커스터마이징

### 🎯 1. 특정 예외만 DLT 전송, 나머지는 무시

```java
DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 0));
handler.addRetryableExceptions(RuntimeException.class);
handler.addNotRetryableExceptions(JsonProcessingException.class); // 역직렬화 오류 무시
```

---

### 🔁 2. 재시도 횟수 조정

```java
// 최대 3번, 1초 간격 재시도
new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
```

---

### 📄 3. 커스텀 로깅 + DLT 토픽 이름 지정

```java
DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
    kafkaTemplate,
    (record, exception) -> {
        System.out.println("⚠️ DLT 전송 대상: " + record.value());
        return new TopicPartition(record.topic() + ".DLT", record.partition());
    }
);
```

---

## 🧩 4. 모든 설정 통합 예시

```java
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
        kafkaTemplate,
        (record, exception) -> {
            System.out.printf("DLT 전송: %s (예외: %s)%n", record.value(), exception.getClass().getSimpleName());
            return new TopicPartition(record.topic() + ".DLT", record.partition());
        }
    );

    DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2));
    handler.addRetryableExceptions(RuntimeException.class);
    handler.addNotRetryableExceptions(IllegalArgumentException.class); // 무시

    return handler;
}
```

---

## ✅ 요약

| 설정 목적 | 사용 방법 |
|-----------|------------|
| 재시도 횟수 조정 | `FixedBackOff(intervalMs, maxAttempts)` |
| 특정 예외만 DLT로 | `addRetryableExceptions()` + `addNotRetryableExceptions()` |
| 로그 및 토픽 지정 | `DeadLetterPublishingRecoverer(record -> topic)` 람다 활용 |

---

## ✅ DLT 메시지 확인 (CLI)

```bash
kafka-console-consumer.sh   --topic order-topic.order-group.DLT   --bootstrap-server localhost:9092   --from-beginning
```

---

# ✅ 끝! 실무에서 장애 메시지를 안전하게 분리해내는 DLT 전략은 필수입니다.
