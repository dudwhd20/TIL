# Redis 실무 학습 로드맵 - 1단계 \~ 3단계

## ✅ 1단계: Redis 기본 개념 및 구조 이해

### Redis란?

* Remote Dictionary Server의 약자
* 인메모리 기반의 Key-Value 저장소
* 매우 빠른 성능 (RAM에 저장됨)
* 싱글 스레드 기반으로 동작

### 주요 특징

* 다양한 자료구조 지원: String, List, Hash, Set, Sorted Set
* TTL(Time-To-Live) 지원
* 영속성 옵션 (RDB, AOF)
* 복제, Sentinel, Cluster로 고가용성 지원
* Pub/Sub 메시징 기능

### 자료구조 요약

| 자료구조       | 설명           | 예시                     |
| ---------- | ------------ | ---------------------- |
| String     | 일반 문자열       | SET key "value"        |
| Hash       | 필드-값 쌍 저장    | HSET user:1 name Alice |
| List       | 순서 있는 리스트    | LPUSH queue job1       |
| Set        | 중복 없는 집합     | SADD tags redis        |
| Sorted Set | 점수 기반 정렬된 집합 | ZADD rank 100 user1    |

### Redis Docker 실행

```bash
docker pull redis
docker run -d --name redis -p 6379:6379 redis
docker exec -it redis redis-cli
```

---

## 🧪 2단계: 기본 명령어 실습

### String

```bash
SET mykey "hello"
GET mykey
DEL mykey
EXISTS mykey
SET count 10
INCR count
DECR count
```

### Hash

```bash
HSET user:1000 name "Alice"
HSET user:1000 age 30
HGET user:1000 name
HGETALL user:1000
HDEL user:1000 age
```

### List

```bash
LPUSH tasks "task1"
LPUSH tasks "task2"
LRANGE tasks 0 -1
RPOP tasks
LPOP tasks
```

### Set

```bash
SADD tags "redis"
SADD tags "nosql"
SISMEMBER tags "redis"
SREM tags "nosql"
SMEMBERS tags
```

* `SISMEMBER`: 특정 값이 Set에 있는지 확인 (1 or 0)
* `SREM`: Set에서 특정 값을 제거

### Sorted Set

```bash
ZADD leaderboard 100 "player1"
ZADD leaderboard 200 "player2"
ZRANGE leaderboard 0 -1 WITHSCORES
ZREVRANGE leaderboard 0 -1 WITHSCORES
ZINCRBY leaderboard 50 "player1"
```

---

## ⏳ 3단계: TTL 기반 캐싱 실습

### TTL 관련 명령어

| 명령어                      | 설명            |
| ------------------------ | ------------- |
| EXPIRE key seconds       | TTL 설정        |
| TTL key                  | 남은 TTL 시간 확인  |
| SET key value EX seconds | TTL 포함 Key 생성 |
| PERSIST key              | TTL 제거        |

### 실습 예제

```bash
SET session:user:1000 "Alice"
EXPIRE session:user:1000 30
TTL session:user:1000

SET cache:weather "sunny" EX 10
PERSIST cache:weather
TTL cache:weather
```

### TTL 결과값 의미

| 값   | 의미                          |
| --- | --------------------------- |
| > 0 | TTL이 설정되어 있고 남은 시간          |
| -1  | TTL이 없음 (무기한 유지)            |
| -2  | Key 자체가 없음 (삭제됨 또는 존재하지 않음) |

---


