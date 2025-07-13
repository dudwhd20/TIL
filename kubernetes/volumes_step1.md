# Kubernetes Volume

## 📌 왜 Volume이 필요한가?

쿠버네티스의 Pod 안에서 실행되는 컨테이너는 기본적으로 상태를 유지하지 않음. 즉, 컨테이너가 재시작되거나 삭제되면 내부에 저장된 데이터도 함께 사라짐.

> ❗ 데이터 유실을 방지하기 위해 Volume을 사용함.

## 📦 Volume이란?

- Volume은 컨테이너가 접근할 수 있는 **공유 저장소**임.
- Pod의 수명과 연관되며, Pod 내 여러 컨테이너가 동일한 Volume을 공유 가능함.

## 📁 주요 Volume 종류

| Volume 타입             | 설명                                                        |
| ----------------------- | ----------------------------------------------------------- |
| `emptyDir`              | Pod가 생성될 때 빈 디렉터리 생성. Pod 삭제 시 데이터 사라짐 |
| `hostPath`              | 노드의 실제 디렉터리를 마운트함. 단일 노드 테스트용         |
| `configMap`             | 설정 데이터를 파일로 마운트                                 |
| `secret`                | 민감한 데이터를 파일로 마운트                               |
| `persistentVolumeClaim` | 영속적인 저장소 요청. PV와 함께 사용 (Step 2에서 설명)      |

## 🛠️ 예제: emptyDir 사용하기

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: vol-demo
spec:
  containers:
    - name: app
      image: busybox
      command: ["/bin/sh", "-c", "echo hello > /data/hello.txt && sleep 3600"]
      volumeMounts:
        - mountPath: /data
          name: my-volume
  volumes:
    - name: my-volume
      emptyDir: {}
```

### ✅ 구조 설명

- `volumes`: Pod 수준에서 정의된 Volume (여기선 `emptyDir`)
- `volumeMounts`: 컨테이너 안에 `/data` 경로로 Volume을 마운트함

## 🔒 hostPath 사용 예시 (주의 필요)

```yaml
volumes:
  - name: host-volume
    hostPath:
      path: /tmp/data
      type: Directory
```

> ⚠️ hostPath는 로컬 노드에 종속되므로 실무에서는 사용 자제

## 📌 정리

- Volume은 Pod 내 컨테이너의 데이터를 보존/공유하기 위한 수단
- 컨테이너 종료 시에도 데이터를 유지하려면 emptyDir이나 hostPath 대신 다음 단계인 PV/PVC 사용 필요

---
