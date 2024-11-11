# PostgreSQL VACUUM과 VACUUM FULL

## 개요
`VACUUM`은 PostgreSQL에서 **불필요한 디스크 공간을 회수하고 성능을 최적화**하기 위해 사용되는 명령입니다. PostgreSQL은 **MVCC(Multi-Version Concurrency Control)** 방식을 사용하여 데이터 변경 시 이전 버전을 삭제하지 않고 새로운 버전을 생성하는데, 이로 인해 **삭제되거나 업데이트된 데이터가 디스크에 남아 있는 dead tuples(불필요한 데이터)**가 쌓이게 됩니다.

## VACUUM이 필요한 이유

### PostgreSQL의 MVCC와 다른 DB와의 차이점
PostgreSQL의 MVCC는 데이터 변경 시 기존 데이터를 삭제하는 대신 **새로운 버전을 테이블에 직접 저장**합니다. 즉, PostgreSQL은 다른 DB와 달리 언두 로그(undo log)를 사용하지 않고 테이블에 직접 버전을 관리하므로, **테이블에 불필요한 dead tuples가 쌓이는 현상**이 발생합니다. 이 때문에 **정기적인 VACUUM 작업**을 통해 dead tuples를 제거하는 것이 필수적입니다.

### VACUUM의 필요성
dead tuples가 쌓이면 테이블 크기가 불필요하게 커지고, 쿼리 성능이 저하될 수 있습니다. `VACUUM`을 수행하지 않으면 디스크 공간이 부족해지거나 인덱스 스캔 시 불필요한 데이터까지 스캔하게 되어 성능이 저하됩니다. 

## 예시: 대량 삭제 후 `VACUUM`과 `VACUUM FULL`의 필요성

### 상황
100만 개의 행이 있는 테이블에서 **단 1개의 행을 제외하고 나머지 모든 행을 삭제**한 상태라고 가정합니다. 이 상태에서 `VACUUM`과 `VACUUM FULL`의 역할을 비교해보겠습니다.

### 문제 발생: dead tuples로 인한 쿼리 성능 저하
대량 삭제 후 `VACUUM`을 수행하지 않으면 PostgreSQL은 삭제된 999,999개의 행을 포함한 전체 100만 개의 행을 스캔하게 됩니다. 이로 인해 쿼리가 오래 걸리고, 디스크 공간을 불필요하게 점유하게 됩니다.

1. **`VACUUM` 미수행 상태의 성능 저하 예시**:
   - 쿼리: `SELECT * FROM table_name WHERE condition;`
   - 예상 실행 시간: **수 초에서 수십 초** (100만 개의 행을 모두 스캔)
   - 디스크 점유 상태: 100만 개의 행이 존재하는 것처럼 공간을 점유 (dead tuples 포함)

2. **`VACUUM` 수행 후**:
   - 쿼리: `SELECT * FROM table_name WHERE condition;`
   - 예상 실행 시간: **밀리초 수준으로 단축** (dead tuples 제거, 유효한 행만 스캔)
   - 디스크 점유 상태: 여전히 dead tuples로 인해 공간이 다소 차지됨 (운영 체제에 반환되지 않음)

3. **`VACUUM FULL` 수행 후**:
   - 쿼리: `SELECT * FROM table_name WHERE condition;`
   - 예상 실행 시간: **밀리초 수준으로 단축**
   - 디스크 점유 상태: **dead tuples 완전 제거, 운영 체제에 디스크 공간 반환**

### 정리
- `VACUUM`: dead tuples를 제거하여 쿼리 성능을 향상시키고, 내부적으로 PostgreSQL이 사용할 수 있는 공간을 확보합니다.
- `VACUUM FULL`: 테이블 전체를 잠그고 데이터를 재정렬하여, **운영 체제에 디스크 공간을 반환**합니다.

## VACUUM FULL의 단점

`VACUUM FULL`은 테이블을 압축하여 공간을 회수할 수 있지만, **단점으로 테이블에 잠금(lock)을 걸기 때문에 서비스에 영향을 줄 수 있습니다**. `VACUUM FULL`이 실행되는 동안 해당 테이블은 다른 쿼리에서 읽기와 쓰기가 모두 불가능하게 됩니다.

### 예시: 테이블 잠금으로 인한 문제
예를 들어, 트래픽이 많은 시간대에 `VACUUM FULL`을 실행한다고 가정해보겠습니다.

1. **상황**: 한 테이블에 `VACUUM FULL` 명령을 수행합니다.
2. **문제 발생**: `VACUUM FULL`이 테이블을 잠그기 때문에, 해당 테이블을 사용하는 다른 쿼리는 `VACUUM FULL`이 완료될 때까지 대기 상태로 남게 됩니다.
3. **결과**: 대량의 쿼리가 대기하게 되며, 서비스의 응답 시간이 길어지고, 결국 시스템 성능에 큰 영향을 미칠 수 있습니다.

### 해결 방법
따라서, `VACUUM FULL`은 **트래픽이 적은 시간대에 실행**하는 것이 좋으며, 대량의 데이터를 삭제하거나 테이블 크기를 줄여야 할 경우 신중하게 사용해야 합니다.


## VACUUM 명령 사용 방법
```sql
-- 기본 VACUUM
VACUUM table_name;

-- VACUUM FULL
VACUUM FULL table_name;

```

## 출처
- PostgreSQL 공식 문서: [VACUUM](https://www.postgresql.org/docs/current/sql-vacuum.html)
- 글 작성 후 Chat Gpt를 활용 하여 정리
