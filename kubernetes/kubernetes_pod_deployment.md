
# Kubernetes 기본 오브젝트 정리 - Pod & Deployment

## 1️⃣ Pod (파드)

### 개념

- Kubernetes에서 배포 가능한 **가장 작은 실행 단위**.
- 하나 이상의 **컨테이너**를 감싸는 단위.
- 같은 Pod 내부 컨테이너들은:
  - **네트워크(IP, Port)를 공유**
  - **스토리지(Volume)를 공유 가능**.

### 예시 YAML

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: my-pod
  labels:
    app: my-app
spec:
  containers:
    - name: my-container
      image: nginx
      ports:
        - containerPort: 80
      env:
        - name: ENV_NAME
          value: production
      volumeMounts:
        - mountPath: /data
          name: my-volume
  volumes:
    - name: my-volume
      emptyDir: {}
```

### 주요 속성

| 속성        | 설명 |
|-------------|------|
| `metadata` | Pod 이름, 라벨(label), 어노테이션 등 |
| `spec` | Pod 내부 구성 정의 |
| `containers` | 컨테이너 목록 (대부분 1개 사용) |
| `volumes` | Pod가 사용할 볼륨 정의 |
| `restartPolicy` | 재시작 정책 (기본: Always) |
| `nodeSelector` | 특정 노드에 Pod 배치 가능 |

---

## 2️⃣ Deployment (디플로이먼트)

### 개념

- **Pod를 직접 관리하지 않고 Deployment로 관리**.
- Pod의 **생성/스케일링/업데이트/롤백** 기능 제공.
- 내부적으로 **ReplicaSet**을 사용하여 Pod 복제본 유지.
- 일반적인 운영 구조:

```
Deployment → ReplicaSet → Pod
```

### 예시 YAML

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-deployment
  labels:
    env: dev
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-app
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 1
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
        - name: my-container
          image: nginx
          ports:
            - containerPort: 80
```

### 주요 속성

| 속성          | 설명 |
|---------------|------|
| `metadata` | Deployment 이름, 라벨(label) 등 |
| `spec.replicas` | Pod 복제본(Replica) 수 |
| `spec.selector.matchLabels` | 어떤 라벨을 가진 Pod를 관리할지 결정 |
| `spec.strategy` | 롤링 업데이트 전략 정의 |
| `spec.template` | 생성할 Pod 템플릿 정의 |

---

## 3️⃣ Deployment 와 Pod 의 Label 연결

### 핵심 연결 구조

```yaml
spec:
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app   # ← selector.matchLabels 와 반드시 일치해야 함
```

### 각 labels 의 역할

| 위치 | 용도 | 대상 |
|-------|-------|-------|
| `Deployment.metadata.labels` | Deployment 리소스 자체에 라벨 부여 (운영/관리용) | Deployment |
| `Deployment.spec.template.metadata.labels` | 생성된 Pod 에 부여할 라벨 (→ selector.matchLabels 와 일치해야 함) | Pod |
| `Deployment.spec.selector.matchLabels` | 어떤 라벨을 가진 Pod 를 관리할지 지정 | Pod 대상 지정 |

### 주의사항

- `Deployment.metadata.labels` 와 Pod 에 부여되는 labels 는 서로 다른 내용일 수 있음.
- 단, `Deployment.spec.selector.matchLabels` 와 `Deployment.spec.template.metadata.labels` 는 반드시 일치해야 Deployment 가 해당 Pod 를 관리 가능.

---

# 요약

| 개념 | 역할 |
|------|------|
| **Pod** | 컨테이너를 실행하는 가장 작은 단위 |
| **Deployment** | 원하는 수의 Pod 복제본 관리, 업데이트, 롤백 등 기능 제공 |
| `Deployment.spec.template` | Deployment 가 생성할 Pod 템플릿 정의 |
| `Deployment.spec.selector.matchLabels` | Deployment 가 어떤 Pod 를 관리할지 결정 |
| Deployment → ReplicaSet → Pod | 자동 연결 구조 |

---

# 다음 추천 학습 순서

1️⃣ Service (Pod → 외부/내부 네트워크 연결)  
2️⃣ ConfigMap / Secret  
3️⃣ Volume (PersistentVolume, PersistentVolumeClaim)  
4️⃣ Ingress (외부 HTTP(S) 노출)  
