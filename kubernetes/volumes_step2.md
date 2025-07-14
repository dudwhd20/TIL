Kubernetes PersistentVolume / PersistentVolumeClaim - Step 2

📦 Step 2: PersistentVolume(PV) / PersistentVolumeClaim(PVC)

컨테이너는 종료되면 데이터가 사라지기 때문에, 영속적인 데이터 저장을 위해 Kubernetes는 PV/PVC 구조를 제공함.

⸻

✅ 핵심 개념

구성 요소 설명
PersistentVolume (PV) 클러스터 관리자가 생성한 실제 저장소 리소스
PersistentVolumeClaim (PVC) 사용자가 요청하는 저장소 계약 (용량, 접근 방식 등)
Pod PVC를 참조하여 실제 PV에 접근함

PVC는 PV를 “소비”하는 방식. EC2 + EBS 개념과 유사함.

⸻

🧱 구성도

[ PV (실제 디스크) ] ← [ PVC (요청서) ] ← [ Pod (사용자) ]

⸻

📄 예시 1: PV 정의

apiVersion: v1
kind: PersistentVolume
metadata:
name: my-pv
spec:
capacity:
storage: 1Gi
accessModes: - ReadWriteOnce
hostPath:
path: /mnt/data

    •	hostPath는 테스트용으로만 사용. 실무에선 EBS, NFS 등을 연결함.

⸻

📄 예시 2: PVC 정의

apiVersion: v1
kind: PersistentVolumeClaim
metadata:
name: my-pvc
spec:
accessModes: - ReadWriteOnce
resources:
requests:
storage: 1Gi

    •	사용자는 PVC를 통해 1Gi 저장소를 요청함
    •	Kubernetes는 조건에 맞는 PV를 자동으로 바인딩함

⸻

📄 예시 3: Pod에서 PVC 사용

apiVersion: v1
kind: Pod
metadata:
name: my-pod
spec:
containers: - name: my-container
image: busybox
command: ["/bin/sh", "-c", "echo hello > /data/hello.txt && sleep 3600"]
volumeMounts: - mountPath: /data
name: storage
volumes: - name: storage
persistentVolumeClaim:
claimName: my-pvc

⸻

🔁 수명 관계

리소스 삭제 시 영향
Pod 삭제 데이터 보존 (PVC/PV는 그대로 유지)
PVC 삭제 PV는 retain/recycle/delete 정책에 따라 정리됨
PV 삭제 수동 정리 필요하거나 오류 발생 가능

⸻

📌 정리
• PV: 클러스터에 미리 존재하는 스토리지 리소스
• PVC: 사용자가 스토리지를 요청하는 선언적 방식
• Pod는 PVC를 통해 PV를 간접적으로 사용
• 실무에선 StorageClass를 통해 동적 프로비저닝도 자주 사용함 (추후 학습)

⸻
