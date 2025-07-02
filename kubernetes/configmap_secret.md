
## 5️⃣ ConfigMap & Secret

### 개념

- Pod나 Deployment에 환경 설정 정보를 전달할 때 사용하는 Kubernetes 오브젝트
- **ConfigMap**: 민감하지 않은 일반 설정값 (환경변수, 설정 파일 등)
- **Secret**: 비밀번호, API 키 등 **민감한 데이터**를 안전하게 저장

---

### ConfigMap

#### 예시 YAML (Key-Value 설정)

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  APP_ENV: "production"
  LOG_LEVEL: "debug"
```

#### Pod에서 사용 예시

```yaml
env:
  - name: APP_ENV
    valueFrom:
      configMapKeyRef:
        name: app-config
        key: APP_ENV
```

#### 마운트 방식 사용 예시

```yaml
volumeMounts:
  - name: config-volume
    mountPath: /etc/config
volumes:
  - name: config-volume
    configMap:
      name: app-config
```

---

### Secret

#### 예시 YAML (base64 인코딩)

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-secret
type: Opaque
data:
  username: dXNlcm5hbWU=   # base64로 인코딩된 "username"
  password: cGFzc3dvcmQ=   # base64로 인코딩된 "password"
```

- base64 인코딩 방법:
  ```bash
  echo -n 'username' | base64
  ```

#### Pod에서 사용 예시 (환경변수)

```yaml
env:
  - name: DB_USER
    valueFrom:
      secretKeyRef:
        name: db-secret
        key: username
```

#### 마운트 방식 사용 예시

```yaml
volumeMounts:
  - name: secret-volume
    mountPath: /etc/secret
    readOnly: true
volumes:
  - name: secret-volume
    secret:
      secretName: db-secret
```

---

### ConfigMap vs Secret 비교

| 항목 | ConfigMap | Secret |
|------|-----------|--------|
| 용도 | 일반 설정값 | 민감한 정보 |
| 데이터 저장 형식 | 평문 (plaintext) | base64 인코딩 |
| 타입 지정 | 필요 없음 | type: Opaque 등 지정 |
| 볼륨 마운트 가능 | ✅ | ✅ |
| 환경변수로 사용 | ✅ | ✅ |

---

### 실무 팁

- Secret은 etcd에 암호화 저장 + Role 제한 필요
- Git에 올릴 때 Secret은 절대 포함 X
- ConfigMap/Secret 변경 시 자동 반영되도록 Deployment에 `checksum` hash 주입하는 패턴도 존재

---

### 실무 구조 예시

```plaintext
[ConfigMap] ─┬─> 환경변수 또는 설정파일로 주입
             └─> Deployment 의 Pod 안으로 mount

[Secret] ────> 민감정보 (DB 비밀번호, API Key 등) 주입
```

