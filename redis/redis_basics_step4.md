🚦 4단계: 고급 Redis 실습 - Pub/Sub 및 분산락

🔔 Pub/Sub (실시간 메시징)

구조

Publisher  -----> Redis Channel -----> Subscriber

명령어 실습

구독자 (Subscriber):

SUBSCRIBE news

발행자 (Publisher):

PUBLISH news "Breaking News: Redis is awesome!"

구독 해제:

UNSUBSCRIBE news

실무 활용 예

실시간 알림 시스템

채팅방 메시지 전송

이벤트 브로커

주의: 구독 중이 아닌 상태에서는 메시지를 받을 수 없음 (영속성 없음)

🔒 분산락 (Distributed Lock)

락 획득

SET lock:resource abc123 NX PX 5000

NX: Key가 없을 때만 SET 실행 (이미 있으면 실패)

PX 5000: 락 유지 시간 5초 (5000ms)

락 실패 시도

SET lock:resource xyz789 NX PX 5000
# => (nil)

TTL 확인

TTL lock:resource

안전한 락 해제 (Lua Script)

EVAL "
  if redis.call('GET', KEYS[1]) == ARGV[1]
  then
    return redis.call('DEL', KEYS[1])
  else
    return 0
  end
" 1 lock:resource abc123

실무 예시

주문 중복 방지

재고 감소 동시성 제어

배치 작업 중복 실행 방지

주의: 단순 DEL은 위험 — 자신이 락을 소유하고 있는지 반드시 확인 필요


