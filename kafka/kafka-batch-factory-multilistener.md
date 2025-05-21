
# 📦 Kafka Batch Listener + Manual Commit 응용 + Listener 커스터마이징

---

## ✅ 1. 배치 리스너 + 수동 커밋 응용 사례

### 🎯 부분 실패 처리

```java
@KafkaListener(topics = "batch-topic", containerFactory = "batchFactory")
public void process(List<String> messages, Acknowledgment ack) {
    List<String> successList = new ArrayList<>();

    for (String msg : messages) {
        try {
            processMessage(msg);
            successList.add(msg);
        } catch (Exception e) {
            sendToDLT(msg, e); // 실패 메시지는 별도 처리
        }
    }

    ack.acknowledge(); // 성공 기준으로 커밋
}
```

---

### 🎯 처리 시간 또는 건수 기준 커밋

```java
long start = System.currentTimeMillis();
int count = 0;

for (String msg : messages) {
    process(msg);
    count++;
    if (count >= 100 || (System.currentTimeMillis() - start) > 2000) {
        break;
    }
}
ack.acknowledge();
```

---

### 🎯 비즈니스 조건 만족 시 커밋

- 예: 성공률 80% 이상이면 커밋, 아니면 DLT

---

## 🛠 2. KafkaListenerContainerFactory 커스터마이징

### ✅ 리스너별 설정 분리

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, String> defaultFactory(
        ConsumerFactory<String, String> consumerFactory) {

    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setConcurrency(3); // 병렬성 기본
    return factory;
}
```

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, String> manualBatchFactory(
        ConsumerFactory<String, String> consumerFactory) {

    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setBatchListener(true);
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // 수동 커밋
    return factory;
}
```

---

### ✅ 리스너별 factory 적용

```java
@KafkaListener(topics = "default-topic", containerFactory = "defaultFactory")
public void handleDefault(String msg) { }

@KafkaListener(topics = "batch-topic", containerFactory = "manualBatchFactory")
public void handleBatch(List<String> msgs, Acknowledgment ack) { }
```

---

## 🔀 3. 다중 리스너 전략

### ✅ 리스너 분리 (토픽별)

```java
@KafkaListener(topics = "order-topic", groupId = "order-group")
public void orderListener(Order order) { }

@KafkaListener(topics = "payment-topic", groupId = "payment-group")
public void paymentListener(Payment payment) { }
```

- 서로 다른 컨슈머 그룹 → 서로 다른 오프셋, 병렬성

---

### ✅ 하나의 리스너로 여러 토픽 처리

```java
@KafkaListener(topics = {"order-topic", "payment-topic"}, groupId = "multi-group")
public void multiListener(String rawMessage,
                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    if (topic.equals("order-topic")) {
        // 주문 처리
    } else if (topic.equals("payment-topic")) {
        // 결제 처리
    }
}
```

- 공통 처리 로직이 있거나, 로그 수집기 등에서 사용됨

---

## ✅ 요약

| 주제 | 내용 |
|------|------|
| 부분 실패 처리 | 성공/실패 분리 후 selective commit |
| Listener 분리 | 토픽/그룹/설정에 따라 나누기 |
| Factory 커스터마이징 | AckMode, batch 여부, 병렬성 분리 가능 |

