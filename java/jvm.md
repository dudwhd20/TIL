# JVM 구조

![alt text](image.png)

클래스 로더 시스템

- .class 에서 바이트 코드를 읽고 메모리에 저장
- 로딩 : 클래스를 읽어 오는 과정
- 초기화: static 값들 초기화 및 변수에 할당

메모리

- 메소드 영역에는 클래스 수준의 정보(클래스 이름, 부모 클래스 이름 ,메소드, 변수) 저장 공유 자원
- 힙영역에는 객체를 저장, 공유 자원
- 스택 영에는 각각 쓰레드마다 런타임 스택을 만들고, 그 안에 메소드 호출을 스택 프레임이라고 부르는 블럭으로 쌓는다 쓰레드가 종료하면 런타임 스택도 사라진다
- PC(Program Counter) 레지스터: 쓰레드 마다 쓰레드 내 현재 실행할 스택 프레임을 가리키는 포인터가 생성
- 네이티브 메소드 스택 : c 나 c++ 로 작성된 메서드를 호출 할 때 사용하는 메모리 영역

실행엔진

- 인터 프리터: 바이트 코드를 한줄 씩 실행
- JIT 컴파일러: 인터프리터 효율을 높이기 위해, 인터프리터가 반복되는 코드를 발견하면 JUT 컴파일러로 반복되는 코드를 모두 네이티브 코드로 변환한다. 이 후 인터프리터는 네이티브 코드로 컴파일된 코드를 바로 사용한다.
- GC(Garbage Collector) : 더이상 참조되지 않은 객체를 모아서 정리

JNI

- java Application 에서 c,c++ 등으로 작성도니 함수를 사용할 수 있는 방법을 제공

네이티브 메소드 라이브러리

- c,c++로 작성된 라이브러리