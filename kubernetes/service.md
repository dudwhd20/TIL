
## 3️⃣ Service (서비스)

### 개념

- Kubernetes 내에서 **Pod를 네트워크로 안정적으로 접근할 수 있게 해주는 오브젝트**.
- Pod는 IP가 바뀌기 때문에 직접 접근하면 안정적이지 않음 → **Service를 통해 고정된 접근 포인트 제공**.
- **Label selector**를 이용해 **어떤 Pod에 트래픽을 전달할지 결정**함.
- **서비스 타입**에 따라 내부, 외부 노출 방식이 달라짐.

---

### 주요 Service 타입

| 타입 | 설명 |
|------|------|
| `ClusterIP` | **기본값**. 클러스터 내부에서만 접근 가능 |
| `NodePort` | 외부에서 접근 가능. 노드의 고정 포트를 통해 서비스 접근 |
| `LoadBalancer` | 클라우드 환경에서 외부 로드밸런서 생성 (GCP, AWS 등) |
| `ExternalName` | 외부 DNS 이름으로 프록시 연결 |

---

### 예시 YAML (ClusterIP)

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  selector:
    app: my-app
  ports:
    - protocol: TCP
      port: 80
      targetPort: 80
  type: ClusterIP
```

---

### 예시 YAML (NodePort)

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-nodeport-service
spec:
  selector:
    app: my-app
  ports:
    - protocol: TCP
      port: 80
      targetPort: 80
      nodePort: 30080
  type: NodePort
```

---

### Pod와 연결 구조

```plaintext
[User or another Pod]
       ↓
    Service (고정된 IP 또는 도메인)
       ↓
   Label Selector로 일치하는 Pod 에 연결
       ↓
   실제 연결 대상: Pod 의 targetPort
```

---

### 주요 속성 정리

| 속성 | 설명 |
|------|------|
| `metadata.name` | 서비스 이름 |
| `spec.selector` | 어떤 Pod 에 트래픽을 보낼지 지정 |
| `spec.ports.port` | 클라이언트가 접속할 포트 (Service IP 기준) |
| `spec.ports.targetPort` | 실제 Pod 내부 컨테이너 포트 |
| `spec.type` | 서비스 종류 (ClusterIP, NodePort 등) |
| `spec.ports.nodePort` | NodePort 타입일 때 외부에서 접근할 포트 번호 |

---

## 4️⃣ 실무형 Service 운영 - DB (ClusterIP)

### 목적

- DB는 외부에 노출하지 않고 내부에서만 접근 가능하도록 관리
- Kubernetes 클러스터 내부에서 접근 → 보안성 높음

---

### 예시 YAML

```yaml
apiVersion: v1
kind: Service
metadata:
  name: db-service
spec:
  selector:
    app: my-db
  ports:
    - protocol: TCP
      port: 5432
      targetPort: 5432
  type: ClusterIP
```

---

### 접근 방식

- backend → db-service 이름으로 접근
- 내부 DNS: `db-service.default.svc.cluster.local:5432`
- 환경변수 예시:

```yaml
env:
  - name: DB_HOST
    value: "db-service"
  - name: DB_PORT
    value: "5432"
```

---

### 외부에서 DB에 접근해야 할 때

| 방식 | 설명 |
|------|------|
| `kubectl port-forward` | 디버깅용. 외부에서 일시적으로 접근 가능 |
| Bastion / VPN | 내부 네트워크로 접근 가능한 환경 구성 |
| LoadBalancer / NodePort | 최후의 수단. 반드시 방화벽, 인증 등 보안 적용 필요 |

---

### 보안 실무 팁

- Secret으로 DB 비밀번호 관리
- NetworkPolicy로 접근 가능한 Pod 제한
- RBAC 최소 권한
- DB 통신 TLS 적용 (선택사항)

---

### 실무 구성 예시

```plaintext
[backend Pod]
     ↓
 [db-service: ClusterIP]
     ↓
 [db Pod (PostgreSQL, etc.)]

외부 개발자 접근 → kubectl port-forward 또는 VPN
```

