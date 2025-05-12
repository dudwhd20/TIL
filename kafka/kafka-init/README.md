# Kafka Spring Boot 연동 실습 (kafka-init)

## ✅ 프로젝트 설명

Kafka 브로커와 연동하여 Spring Boot에서 간단한 문자열 메시지를 주고받는 기본 예제입니다.

* 프로젝트 이름: `kafka-init`
* 프레임워크: Spring Boot 3.2.5
* 빌드 도구: Gradle
* Java 버전: 17
* Kafka 버전: 3.8.1

---

## 📁 주요 구성 파일

### 🔧 `build.gradle`

```groovy
plugins {
    id 'org.springframework.boot' version '3.2.5'
    id 'io.spring.dependency-management' version '1.1.4'
    id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.kafka:spring-kafka'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.kafka:spring-kafka-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

### 🛠️ `application.yml`

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: my-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

---

## 📄 주요 클래스

### ✅ `KafkaProducerService.java`

```java
@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
```

### ✅ `KafkaConsumerListener.java`

```java
@Component
public class KafkaConsumerListener {
    @KafkaListener(topics = "spring-test", groupId = "my-group")
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println("[Consumer] Received: " + record.value());
    }
}
```

### ✅ `KafkaController.java`

```java
@RestController
@RequestMapping("/send")
public class KafkaController {
    private final KafkaProducerService producerService;
    public KafkaController(KafkaProducerService producerService) {
        this.producerService = producerService;
    }
    @PostMapping
    public String sendMessage(@RequestParam String message) {
        producerService.sendMessage("spring-test", message);
        return "Message sent: " + message;
    }
}
```

---

## 🚀 실행 명령어

### ✅ 1. Zookeeper 실행 (터미널 1)

```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

### ✅ 2. Kafka 브로커 실행 (터미널 2)

```bash
bin/kafka-server-start.sh config/server.properties
```

### ✅ 3. Spring Boot 앱 실행 (터미널 3)

```bash
./gradlew bootRun
```

### ✅ 4. 메시지 전송 테스트

```bash
curl -X POST "http://localhost:8080/send?message=hello-from-postman"
```

> 콘솔 출력 예시:

```
[Consumer] Received: hello-from-postman
```

---
