Kubernetes 1.27 Single Node Cluster on Rocky Linux (Mac M1 + UTM)

⸻

프로젝트 가이드

가상화 리눅스(오프라이드 VM)에서 Kubernetes를 학습하기 위한 설치 방법을 정리한다.
	•	환경: Mac M1 + UTM
	•	VM: Rocky Linux (ARM64, aarch64)
	•	Container Runtime: containerd
	•	Kubernetes version: v1.27
	•	Network: Calico
	•	UI / Metrics: Dashboard, Metrics Server

⸻

사전 준비

Rocky Linux 기본 설정

echo '======== [4] Rocky Linux 기본 설정 ========'
echo '======== [4-2] 타임존 설정 ========'
timedatectl set-timezone Asia/Seoul

echo '======== [4-3] [WARNING FileExisting-tc]: tc not found in system path 로그 관련 업데이트 ========'
yum install -y yum-utils iproute-tc

echo '======= [4-4] hosts 설정 =========='
cat << EOF >> /etc/hosts
192.168.56.30 k8s-master
192.168.56.31 k8s-worker1
192.168.56.32 k8s-worker2
EOF

폴라우 / Swap / sysctl 설정

echo '======== [5] 방화벽 해제 ========'
systemctl stop firewalld && systemctl disable firewalld

echo '======== [5] Swap 비활성화 ========'
swapoff -a && sed -i '/ swap / s/^/#/' /etc/fstab


⸻

Container Runtime (containerd) 설정

echo '======== [6-1] iptable 세팅 ========'
cat <<EOF |tee /etc/modules-load.d/k8s.conf
overlay
br_netfilter
EOF

modprobe overlay
modprobe br_netfilter

cat <<EOF |tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-iptables  = 1
net.bridge.bridge-nf-call-ip6tables = 1
net.ipv4.ip_forward                 = 1
EOF

sysctl --system

echo '======== [6-2-1-1] containerd 패키지 설치 ========'
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

yum install -y containerd.io-1.6.21-3.1.el9.aarch64
systemctl daemon-reload
systemctl enable --now containerd

# Systemd cgroup 허용
echo '======== [6-3] 콘텐이너 런타임 cri 활성화 ========'
containerd config default > /etc/containerd/config.toml
sed -i 's/ SystemdCgroup = false/ SystemdCgroup = true/' /etc/containerd/config.toml
systemctl restart containerd


⸻

Kubernetes Component 설정

echo '======== [7] repo 설정 ========'
cat <<EOF | sudo tee /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://pkgs.k8s.io/core:/stable:/v1.27/rpm/
enabled=1
gpgcheck=1
gpgkey=https://pkgs.k8s.io/core:/stable:/v1.27/rpm/repodata/repomd.xml.key
exclude=kubelet kubeadm kubectl cri-tools kubernetes-cni
EOF

echo '======== [7] SELinux 설정 ========'
setenforce 0
sed -i 's/^SELINUX=enforcing$/SELINUX=permissive/' /etc/selinux/config

echo '======== [7] kubelet, kubeadm, kubectl 패키지 설치 ========'
yum install -y kubelet-1.27.2-150500.1.1.aarch64 kubeadm-1.27.2-150500.1.1.aarch64 kubectl-1.27.2-150500.1.1.aarch64 --disableexcludes=kubernetes
systemctl enable --now kubelet


⸻

Kubernetes Cluster 구성 (Master Node)

echo '======== [8-1] 클러스터 초기화 (Pod Network 세팅) ========'
kubeadm init --pod-network-cidr=20.96.0.0/16 --apiserver-advertise-address 192.168.56.30

# Join command 생성
kubeadm token create --print-join-command > ~/join.sh

echo '======== [8-2] kubectl 사용 설정 ========'
mkdir -p $HOME/.kube
cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
chown $(id -u):$(id -g) $HOME/.kube/config


⸻

Pod Network (Calico) 설치

echo '======== [8-3] Pod Network 설치 (calico) ========'
kubectl create -f https://raw.githubusercontent.com/k8s-1pro/install/main/ground/k8s-1.27/calico-3.26.4/calico.yaml
kubectl create -f https://raw.githubusercontent.com/k8s-1pro/install/main/ground/k8s-1.27/calico-3.26.4/calico-custom.yaml


⸻

Kubernetes 편의 기능 설치

kubectl 자동완성 / alias

echo '======== [9-1] kubectl 자동완성 기능 ========'
yum -y install bash-completion
echo "source <(kubectl completion bash)" >> ~/.bashrc
echo 'alias k=kubectl' >>~/.bashrc
echo 'complete -o default -F __start_kubectl k' >>~/.bashrc
source ~/.bashrc

Dashboard 설치

echo '======== [9-2] Dashboard 설치 ========'
kubectl create -f https://raw.githubusercontent.com/k8s-1pro/install/main/ground/k8s-1.27/dashboard-2.7.0/dashboard.yaml

Metrics Server 설치

echo '======== [9-3] Metrics Server 설치 ========'
kubectl create -f https://raw.githubusercontent.com/k8s-1pro/install/main/ground/k8s-1.27/metrics-server-0.6.3/metrics-server.yaml


⸻

결과 확인

echo '======== [10-1] Pod 상태 확인 ========'
kubectl get pod -A

echo '======== [10-2] Node Ready 확인 ========'
kubectl get nodes -o wide


⸻

추가 설정 (선택)

Master Node에도 Pod Scheduling 허용 (Single Node Cluster용)

kubectl taint nodes --all node-role.kubernetes.io/control-plane-
kubectl taint nodes --all node-role.kubernetes.io/master-


⸻

참고 명령어

# Deployment 예시 생성
kubectl create deployment nginx --image=nginx

# 서비스 노출
kubectl expose deployment nginx --port=80 --type=NodePort

# 상태 확인
kubectl get pods
kubectl get svc


⸻

