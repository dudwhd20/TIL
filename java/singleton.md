GOF 생성 패턴 중 하나인 싱글톤 패턴은 어떤 클래스의 인스턴스는 하나임을 보장하는 패턴이다

java에서 구현하기 제일 쉬운 방법은 

![image](https://github.com/user-attachments/assets/e7e0c63e-4a5f-49ef-84a4-691de9efcf3a)

위와 같이 생성자를 private로 생성하고 static 메서드를 이용하여 인스턴스 유무를 확인 후 반환 하는  방법이다

하지만 위와 같이 작성 시 두개의 쓰레드가 동시 접근 시 인스턴스가 동일한 인스턴스임을 보장받지 못 할 수도 있다

그래서 inner Class를 활용한 생성 방법을 추천하고 있다

![image](https://github.com/user-attachments/assets/c980ee55-b707-45a0-b647-676041ff2883)


이런식으로 작성시 인스턴스 생성 함수에 synchronized 키워드를 사용하지 않아서 성능적으로도 좋고 
동시에 두 개 이상의 쓰레드으 접근에서도 동일한 인스턴스를 보장 받아 안전하다
