# 컴퓨터 네트워크 과제 [HTTP 구현]

## 동작 환경

- JDK 21
- Gradle 8.13
- MacOS 26.0

## 프로젝트 구조

gradle 멀티 모듈 프로젝트로 구성되어 있습니다.

- **common**: HTTP 요청 및 응답 객체 (HttpRequest, HttpResponse)와 각종 상수(HttpMethod, HttpStatus, HttpVersion) 등 공통적으로 사용되는 기능을 포함합니다.
- **client**: HTTP 클라이언트 기능을 포함합니다. 시나리오에 따라 HTTP 요청을 보내고 응답을 받을 수 있습니다.
- **server**: HTTP 서버 기능을 포함합니다. 클라이언트의 요청을 처리하고 응답을 반환합니다.

### 파일 트리

```
├── common
│   ├── src
│       └── main
│           └── java
│               └── com.github.son_daehyeon.computer_network_socket.common
│                   ├── constant
│                   │   ├── HttpMethod.java
│                   │   ├── HttpStatus.java
│                   │   └── HttpVersion.java
│                   ├── http
│                   │   ├── HttpRequest.java
│                   │   └── HttpResponse.java
│                   └── util
│                       └── JsonMapper.java
│   └── build.gradle
│
│
├── client
│   ├── src
│       └── main
│           └── java
│               └── com.github.son_daehyeon.computer_network_socket.client
│                   ├── Client.java
│                   ├── scenario
│                   │   ├── Scenario.java
│                   │   ├── ScenarioList.java
│                   │   └── ScenarioStorage.java
│                   └── util
│                       ├── LineBreaker.java
│                       └── Requester.java
│   ├── gradle
│   │   └── wrapper
│   │       ├── gradle-wrapper.jar
│   │       └── gradle-wrapper.properties
│   ├── build.gradle
│   ├── gradlew
│   └── gradlew.bat
│
│
├── server
│   ├── src
│       └── main
│           ├── java
│           │   └── com.github.son_daehyeon.computer_network_socket.server
│           │       ├── domain
│           │       │   ├── controller
│           │       │   │   └── Controller.java
│           │       │   ├── middleware
│           │       │   │   └── AuthMiddleware.java
│           │       │   ├── repository
│           │       │   │   ├── SessionRepository.java
│           │       │   │   └── UserRepository.java
│           │       │   ├── schema
│           │       │   │   └── User.java
│           │       │   └── service
│           │       │       └── Service.java
│           │       ├── router
│           │       │   ├── RequestMapping.java
│           │       │   ├── Route.java
│           │       │   └── Router.java
│           │       ├── util
│           │           ├── ApiException.java
│           │           ├── BodyValidator.java
│           │           ├── MySQL.java
│           │           └── Redis.java
│           │       └── Server.java
│           └── resources
│               └── initialization.sql
│
│
│   ├── gradle
│   │   └── wrapper
│   │       ├── gradle-wrapper.jar
│   │       └── gradle-wrapper.properties
│   ├── build.gradle
│   ├── gradlew
│   └── gradlew.bat
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle
├── settings.gradle
├── gradlew
└── gradlew.bat
```