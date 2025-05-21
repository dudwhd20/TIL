# Kafka Key 기반 메시지 라우팅 이론 및 CLI 실습

## 1. Kafka 구성 요소 요약

* **Producer**: 메시지를 생성해 Kafka로 보냄
* **Consumer**: Kafka에서 메시지를 읽어 처리
* **Broker**: Kafka 서버, 메시지를 저장
* **Topic**: 메시지를 분류하는 논리적 단위
* **Partition**: Topic을 나누는 물리적 단위 (병렬성 단위)

---

## 2. Key 기반 메시지 라우팅 이론

### ✅ Key의 역할

* Kafka는 메시지를 보낼 때 Key가 있으면 `hash(key) % partition 수`로 파티션을 결정함
* 같은 Key는 항상 같은 파티션에 저장됨 → **순서 보장 가능**

### ✅ Key 기반 파티션 예시

| Key   | Partition 수 | 결과 Partition |
| ----- | ----------- | ------------ |
| user1 | 3           | partition-0  |
| user2 | 3           | partition-2  |
| user1 | 3           | partition-0  |

---

## 3. CLI 실습 과정

### ✅ 토픽 생성 (3개의 파티션)

```bash
kafka-topics.sh --bootstrap-server localhost:9092 \
  --create --topic test-topic --partitions 3 --replication-factor 1
```

### ✅ Producer 실행 (Key 포함)

```bash
kafka-console-producer.sh \
  --broker-list localhost:9092 \
  --topic test-topic \
  --property "parse.key=true" \
  --property "key.separator=:"
```

입력 예시:

```
user1:hello1
user2:hello2
user1:hello3
user3:hello4
```

### ✅ Consumer 실행 (파티션 출력 확인)

```bash
kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic test-topic \
  --from-beginning \
  --property print.key=true \
  --property print.partition=true
```

출력 예시:

```
Partition:2  user2  hello2
Partition:2  user3  hello4
Partition:0  user1  hello1
Partition:0  user1  hello3
```

→ 같은 Key (`user1`)는 항상 같은 파티션에 들어감 → 순서 보장 OK

---

## 4. Key + Value 내 Sequence 전략

* Kafka는 파티션 단위로만 순서를 보장함
* 비즈니스 의미의 순서를 유지하려면 메시지 안에 `seq`, `timestamp` 등을 포함시키는 게 안전

```json
{
  "userId": "user123",
  "seq": 45,
  "eventType": "checkout"
}
```

---

## 5. 토픽 생성 방법 정리

| 방식                 | 특징                          | 실무 적용   |
| ------------------ | --------------------------- | ------- |
| CLI                | 명시적 제어, 실습 및 운영 시 안정적       | ✅ 많이 사용 |
| Java (AdminClient) | 코드 기반 자동화, 테스트, SaaS 환경에 적합 | ✅ 일부 사용 |
| 자동 생성              | 오타/오용 가능성 → 실무에서는 꺼두는 편     | ❌ 비권장   |

Kafka 설정 예시 (자동 생성 끄기):

```
auto.create.topics.enable=false
```

---

## ✅ 정리 요약

* Key가 있으면 Kafka는 해시를 통해 파티션을 결정하고, 같은 Key는 항상 같은 파티션에 저장됨
* 파티션 내부는 순서가 보장되며, 이로 인해 **동일 Key에 대한 순차 처리**가 가능함
* CLI를 통해 이 과정을 직접 실습해보며 시각적으로 확인함
* 실무에서는 Key 외에도 메시지에 `seq`, `timestamp` 등을 포함해 정합성을 관리함
* 토픽 생성은 일반적으로 CLI나 AdminClient로 명시적으로 처리하며, 자동 생성은 꺼두는 것이 안전함

