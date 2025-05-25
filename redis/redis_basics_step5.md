Redis 고급 실습: Stream (Kafka 유사 구조)

📊 Redis Stream이란?
	•	시간 순서대로 데이터를 저장하는 로그 구조
	•	메시지를 쌓고, 읽고, 그룹 단위로 처리 가능 (Kafka 유사)
	•	Redis 5.0부터 지원
	•	Kafka처럼 메시지를 소비하고, 재처리하거나 순서를 관리 가능

⸻

🧪 기본 실습 명령어

1. 메시지 추가 (XADD)

XADD mystream * sensor-id 1234 temperature 22.5

	•	XADD: Stream에 데이터 추가
	•	mystream: 스트림 이름 (Key)
	•	*: Redis가 자동으로 메시지 ID 생성
	•	sensor-id, temperature: 필드 이름과 값 쌍

2. 메시지 읽기 (XREAD)

XREAD COUNT 2 STREAMS mystream 0

	•	COUNT 2: 최대 2개 메시지 읽기
	•	STREAMS mystream 0: mystream 스트림에서 ID 0부터 읽기

⸻

👥 Consumer Group 사용

3. Consumer 그룹 생성 (Kafka 유사)

XGROUP CREATE mystream mygroup 0 MKSTREAM

	•	mygroup: Consumer Group 이름
	•	0: 스트림 처음부터 메시지를 읽게 설정
	•	MKSTREAM: 스트림이 없다면 생성

4. 그룹 기반 메시지 읽기 (XREADGROUP)

XREADGROUP GROUP mygroup consumer1 COUNT 2 STREAMS mystream >

	•	GROUP mygroup consumer1: 그룹 이름과 소비자 이름 지정
	•	COUNT 2: 최대 2개 메시지 읽기
	•	>: 아직 읽지 않은 새 메시지만 소비

5. 메시지 확인 처리 (XACK)

XACK mystream mygroup <message-id>

	•	XACK: 메시지를 처리 완료했음을 Redis에 알림
	•	<message-id>: XREADGROUP 결과로 받은 메시지 ID

⸻

⚙️ 용어 매핑 (Kafka vs Redis)

Kafka	Redis Stream
Topic	Stream (Key)
Consumer Group	XGROUP
Offset	메시지 ID
Commit	XACK


⸻

🧠 실무 예시
	•	실시간 로그 수집 및 처리
	•	사용자 이벤트 큐잉
	•	채팅 메시지 저장
	•	분산 작업 대기열 (Job Queue)

⸻

⚠️ 주의사항
	•	메시지는 메모리에 쌓이므로, MAXLEN 옵션 또는 수동 삭제 필요
	•	XACK를 하지 않으면 메시지는 처리된 것으로 간주되지 않음 (재전송 가능)

⸻

✅ 실습 환경 구성 팁
	•	Docker로 Redis 띄우고 redis-cli를 두 개 실행
	•	하나는 Producer (XADD), 하나는 Consumer (XREADGROUP 또는 XREAD)

⸻

Redis Stream은 Pub/Sub보다 안정적이고 유실되지 않으며, Kafka만큼은 아니더라도 중간급 로그 처리에 매우 유용합니다.
