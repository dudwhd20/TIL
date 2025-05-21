
# 📦 Kafka Batch Listener + Manual Commit 연동

## ✅ 목적
Kafka에서 메시지를 한 번에 여러 개(batch)로 받아 처리하고,  
모두 성공적으로 처리된 경우에만 수동으로 오프셋을 커밋하는 구조를 구현한다.

---

## 🎯 사용하는 이유

| 상황 | 이유 |
|------|------|
| ✅ 메시지를 배치로 처리하고 싶음 | 성능 향상 (DB bulk insert 등) |
| ✅ 실패 시 재처리 가능하게 하고 싶음 | 커밋을 수동으로 하여, 실패 시 메시지를 재수신 |

---

## 🧱 전체 구조 예시

```java
@KafkaListener(
  topics = "batch-topic",
  containerFactory = "batchFactory"
)
public void listen(List<String> records, Acknowledgment ack) {
    // 메시지 처리
    ack.acknowledge(); // 수동 커밋
}
```

---

## ✅ 1. ListenerContainerFactory 설정

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, String> batchFactory(
        ConsumerFactory<String, String> consumerFactory) {

    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);

    factory.setBatchListener(true); // ✅ 배치 리스너
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // ✅ 수동 커밋

    return factory;
}
```

---

## ✅ 2. @KafkaListener 구현

```java
@KafkaListener(
  topics = "batch-topic",
  containerFactory = "batchFactory"
)
public void processBatch(List<String> messages, Acknowledgment ack) {
    try {
        for (String msg : messages) {
            System.out.println("처리 중: " + msg);
            // TODO: 처리 로직
        }

        ack.acknowledge(); // 👈 모두 성공 시 커밋
        System.out.println("✅ 배치 처리 및 커밋 완료");

    } catch (Exception e) {
        System.err.println("❌ 배치 처리 실패. 커밋 생략됨");
        // 커밋 안 하면 다음 poll 시 재전송됨
    }
}
```

---

## 🧪 실험 포인트

| 조건 | 결과 |
|------|------|
| ack 호출 생략 | 메시지 다시 전달됨 |
| ack 전에 예외 발생 | 커밋되지 않음 → 메시지 다시 수신 |
| ack 이후 예외 발생 | 이미 커밋됨 → 메시지 재수신되지 않음 |

---

## 🧠 실무 응용 전략

| 상황 | 해결 방안 |
|------|-----------|
| 부분 실패 | 성공 메시지만 따로 모아 ack + 실패는 DLT |
| 커밋 시점 커스터마이징 | 메시지 수, 처리 시간, 로직 완료 기준으로 분기 |
| 병렬성 향상 | `concurrency` 설정으로 병렬 처리 + batch 적용 가능 |

---

## ✅ 요약
- `@KafkaListener`에서 `List<T>`와 `Acknowledgment`를 함께 사용하면 **배치 + 수동 커밋** 가능
- 커밋 타이밍을 직접 제어하므로 **정확한 메시지 처리 제어** 가능
