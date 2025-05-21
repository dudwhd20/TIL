### 📄 `kafka-consumer-groups.sh` 실습 정리

#### 📌 실습 환경
- **브로커 주소**: `localhost:9092`
- **토픽 이름**: `reset-test`
- **컨슈머 그룹**: `reset-group`

---

## ✅ 컨슈머 그룹 상태 확인

### 토픽 생성
```bash
kafka-topics.sh --create --topic reset-test --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

### 메시지 전송
```bash
kafka-console-producer.sh --topic reset-test --bootstrap-server localhost:9092
```
입력 예:
```
one
two
three
```

### 컨슈머 실행 (처음부터 읽기)
```bash
kafka-console-consumer.sh --topic reset-test --bootstrap-server localhost:9092 --group reset-group --from-beginning
```

### 컨슈머 오프셋 확인
```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group reset-group
```

---

## 🔁 오프셋 리셋 실습

### 1️⃣ 오프셋 리셋 미리보기 (`--dry-run`)
```bash
kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group reset-group \
  --topic reset-test \
  --reset-offsets \
  --to-earliest \
  --dry-run
```

### 2️⃣ 오프셋 리셋 실행 (`--execute`)
```bash
kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group reset-group \
  --topic reset-test \
  --reset-offsets \
  --to-earliest \
  --execute
```

### 3️⃣ 리셋 이후 컨슈머 실행 (재확인)
```bash
kafka-console-consumer.sh --topic reset-test --bootstrap-server localhost:9092 --group reset-group
```

> 리셋된 오프셋 덕분에 이전 메시지(`one`, `two`, `three`)가 다시 출력됨

---

## 🧠 오프셋 리셋 옵션 요약

| 옵션 | 설명 |
|------|------|
| `--to-earliest` | 가장 오래된 메시지부터 다시 읽음 |
| `--to-latest` | 가장 최신 메시지 이후부터 시작 |
| `--shift-by <n>` | 현재 오프셋을 ±n 만큼 이동 |
| `--to-offset <n>` | 특정 오프셋 번호로 이동 |
| `--by-duration <PT5M>` | 특정 시간 전으로 이동 (예: 5분 전) |
| `--dry-run` | 결과 미리보기 (실행 안 됨) |
| `--execute` | 실제 오프셋 리셋 실행 |

---

## ⚠️ 주의사항
- 컨슈머가 실행 중이면 리셋이 적용되지 않을 수 있음
- 테스트 시 `--dry-run`으로 먼저 결과를 확인하는 습관을 들이자
