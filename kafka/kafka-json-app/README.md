# Kafka JSON 메시지 전송 실습 정리 (Spring Boot 기반)

## ✅ 학습 목표

* Kafka 개발 복서 (Broker, Topic, Partition, Consumer Group 등)
* Spring Boot 기반 Kafka JSON 메시지 송수신 구현
* Kafka CLI와 연동하여 메시지 확인

---

## 🧠 Kafka 해석 개발 요약

| 구성 요소          | 설명                                         |
| -------------- | ------------------------------------------ |
| Broker         | Kafka 서버 프로세스. 메시지를 저장하고 전송                |
| Topic          | 메시지가 전송되는 놀릭적인 채널                          |
| Partition      | 하나의 토픽을 분산 저장하는 단위. 병력 처리 가능               |
| Zookeeper      | Kafka 클러스터 메타데이터 관리. (Kafka 3.x 이후 제거 가능)  |
| Producer       | 메시지를 Kafka로 전송하는 애플리케이션                    |
| Consumer       | Kafka에서 메시지를 수신하는 애플리케이션                   |
| Consumer Group | 같은 Group ID를 가지는 Consumer 집합. 파티션 분산 처리 가능 |

---

## 🛠 프로젝트 구조

```
kafka-json-app
├── config          # Kafka 설정
├── consumer        # Kafka Consumer
├── model           # OrderEvent 등 메시지 모델
├── producer        # Kafka Producer
└── controller      # 테스트용 REST API
```

---

## ⚙️ build.gradle 설정

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.kafka:spring-kafka'
    implementation 'com.fasterxml.jackson.core:jackson-databind'
    implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310' // LocalDateTime 지열화 지원
}
```

---

## ✅ 메시지 모델 클래스

```java
public class OrderEvent {
    private String orderId;
    private String productId;
    private int quantity;
    private LocalDateTime orderedAt;
    // 생성자, getter, toString()
}
```

---

## ✅ Kafka 설정

### ProducerConfig

```java
@Bean
public ProducerFactory<String, OrderEvent> producerFactory() {
    return new DefaultKafkaProducerFactory<>(Map.of(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
    ));
}
```

### ConsumerConfig

```java
@Bean
public ConsumerFactory<String, OrderEvent> consumerFactory() {
    JsonDeserializer<OrderEvent> deserializer = new JsonDeserializer<>(OrderEvent.class);
    deserializer.setRemoveTypeHeaders(false);
    deserializer.addTrustedPackages("*");
    deserializer.setUseTypeMapperForKey(true);

    return new DefaultKafkaConsumerFactory<>(
        Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
            ConsumerConfig.GROUP_ID_CONFIG, "order-group"
        ),
        new StringDeserializer(),
        deserializer
    );
}
```

---

## ✅ 메시지 전송 / 수신 구현

### Producer

```java
@Service
public class OrderEventProducer {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    public void sendOrder(OrderEvent orderEvent) {
        kafkaTemplate.send("order-events", orderEvent.getOrderId(), orderEvent);
    }
}
```

### Consumer

```java
@Service
public class OrderEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    @KafkaListener(topics = "order-events", groupId = "order-group")
    public void consume(OrderEvent orderEvent) {
        log.info("✅ Received OrderEvent: {}", orderEvent);
    }
}
```

---

## ✅ REST API 테스트

```java
@RestController
@RequestMapping("/orders")
public class OrderController {
    @PostMapping
    public String createOrder(@RequestParam String productId, @RequestParam int quantity) {
        OrderEvent event = new OrderEvent(...);
        producer.sendOrder(event);
        return "Sent: " + event;
    }
}
```

```bash
# 테스트 명령어
curl -X POST "http://localhost:8080/orders?productId=apple&quantity=3"
```

---

## 🥮 Kafka CLI로 메시지 확인

```bash
bin/kafka-console-consumer.sh \
  --topic order-events \
  --bootstrap-server localhost:9092 \
  --from-beginning
```

---

## 🧰 보너스 개발 개발 요약

* Kafka Consumer는 기본적으로 **하나의 클래스 타입**만 역지리설라이화함.
* 여러 타입을 처리하려면:

  * `__TypeId__` 헤더 + Generic Deserializer
  * 또는 Consumer에서 문자열로 받고 수동으로 분기 처리

---

## 📜 학습 완료 기준

* [x] Kafka JSON 메시지 전송 구현
* [x] Consumer 설정 및 수신 확인
* [x] CLI를 통한 메시지 내용 검사
* [x] 역지리설라이즘 오류 해결 경험

```

}

```
