# Gradle 핵심 개념 7단계 요약


---

## ✅ 1. Gradle이란?

* 빌드 자동화 도구로서 컴파일, 테스트, 패키징, 배포까지 지원
* Groovy 또는 Kotlin DSL 기반으로 작성됨
* Maven보다 유연하고, 빌드 속도가 빠름 (incremental build, daemon 등)

---

## ✅ 2. `build.gradle` 구조

Gradle 프로젝트의 핵심 설정 파일로, DSL 방식으로 작성됨.

```groovy
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0.0'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.apache.commons:commons-lang3:3.12.0'
}

tasks.register('hello') {
    doLast {
        println 'Hello Gradle!'
    }
}
```

---

## ✅ 3. dependencies / repositories

### repositories

* 외부 라이브러리를 받아오는 위치를 정의
* 예: `mavenCentral()`, `google()`

```groovy
repositories {
    mavenCentral()
}
```

### dependencies

* 사용할 라이브러리를 정의
* 예: `implementation`, `testImplementation`, `runtimeOnly` 등

```groovy
dependencies {
    implementation 'com.google.guava:guava:31.1-jre'
    testImplementation 'junit:junit:4.13.2'
}
```

---

## ✅ 4. Task 시스템

* Gradle은 Task 기반으로 작동
* Task = 실행 가능한 작업 단위
* 커스텀 Task를 정의해서 유틸성 작업이나 빌드 후 처리를 할 수 있음

```groovy
tasks.register('hello') {
    doLast {
        println 'Hello Gradle!'
    }
}
```

* `doFirst {}`: Task 시작 전에 실행
* `doLast {}`: Task 끝날 때 실행

---

## ✅ 5. Plugin 시스템

* 플러그인을 통해 Gradle 기능을 확장할 수 있음
* 예: `java`, `application`, `org.springframework.boot`

```groovy
plugins {
    id 'java'
    id 'application'
}
```

---

## ✅ 6. Gradle Wrapper

* Gradle을 설치하지 않아도 프로젝트별로 실행 가능하게 하는 도구
* 프로젝트 내에 `gradlew`, `gradlew.bat`, `gradle/wrapper` 디렉토리 포함됨
* 명령어:

```bash
gradle wrapper --gradle-version 8.7
./gradlew build
```

* 설정 파일: `gradle-wrapper.properties`

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.7-bin.zip
```

---

## ✅ 7. 멀티 모듈 구성

* 하나의 루트 프로젝트에 여러 서브 모듈을 포함시켜 관리

### settings.gradle

```groovy
rootProject.name = 'multi-module-project'
include 'common', 'core', 'api'
```

### build.gradle에서 모듈 간 의존성 연결

```groovy
dependencies {
    implementation project(':core')
}
```

* 각 모듈은 자신의 `build.gradle`을 가지고 독립적으로 구성됨
* 공통 설정은 루트 `build.gradle`의 `subprojects {}` 블록에서 관리

---


