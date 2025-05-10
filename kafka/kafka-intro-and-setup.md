# Kafka Intro and Setup

## 📌 Kafka Core Concepts

### ✅ Broker

* Kafka 클러스터 내의 하나의 서버 단위
* 프로듀서로부터 메시지를 받아 디스크에 저장하고, 컨슈머에게 전달함
* 장애 대응, 내구성 확보, 분산 저장의 핵심

### ✅ Topic

* 메시지를 분류하는 논리적 단위
* 카카오톡의 채팅방처럼 동작
* 프로듀서가 메시지를 특정 토픽으로 전송, 컨슈머는 해당 토픽만 구독 가능

### ✅ Partition

* 토픽을 구성하는 물리적 저장 단위
* 병렬 처리, 저장 분산, 확장성 확보 가능
* Kafka는 메시지를 여러 파티션에 나누어 저장하고, 각 파티션은 브로커에 분산됨

### ✅ Consumer Group

* 컨슈머를 묶는 논리 단위
* 그룹 내에서는 파티션이 컨슈머들에게 분산되어 처리됨
* 하나의 메시지는 그룹 내에서 단 1명의 컨슈머만 처리
* 그룹이 다르면 같은 메시지를 모두 받을 수도 있음 (Pub/Sub 형태)

---

## ⚙️ Kafka 설치 (macOS 기준, Kafka 3.8.1)

### ✅ 사전 준비

* Java 8 이상 설치
* `wget` 설치 필요 시:

  ```bash
  brew install wget
  ```

### ✅ Kafka 다운로드 및 압축 해제

```bash
wget https://downloads.apache.org/kafka/3.8.1/kafka_2.13-3.8.1.tgz
tar -xzf kafka_2.13-3.8.1.tgz
cd kafka_2.13-3.8.1
```

---

## 🚀 Kafka 실행

### ✅ Zookeeper 실행 (터미널 1)

```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

### ✅ Kafka 브로커 실행 (터미널 2)

```bash
bin/kafka-server-start.sh config/server.properties
```

### ✅ 실행 확인

```bash
bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
```

> 결과가 없다면 정상. 아직 토픽이 없음

---

## 🧪 테스트용 토픽 생성

```bash
bin/kafka-topics.sh --bootstrap-server localhost:9092 \
--create --topic test-topic --partitions 1 --replication-factor 1
```

> 토픽 이름: `test-topic`
> 파티션: 1개
> 복제본: 1개 (로컬 환경이므로 1로 설정)

