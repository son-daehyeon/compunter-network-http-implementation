# 컴퓨터 네트워크 과제 [HTTP 구현]

> **학기**: 2025년 여름학기 <br/>
> **학번**: 20243156 <br/>
> **이름**: 손대현 <br/>
> **Github Repository**: [son-daehyeon/computer-network-http-implementation](https://github.com/son-daehyeon/computer-network-http-implementation)

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

## 외부 라이브러리

기본적은 HTTP 통신 외 부가적으로 필요한 기능을 위해 외부 라이브러리를 사용했습니다.

### 모든 모듈

- [org.projectlombok:lombok:1.18.38](https://mvnrepository.com/artifact/org.projectlombok/lombok/1.18.38): 보일러플레이트 코드를 줄이기 위해 사용했습니다.

### 공통 모듈

- [com.google.code.gson:gson:2.13.1](https://mvnrepository.com/artifact/com.google.code.gson/gson/2.13.1): JSON 직렬화 및 역직렬화를 위해 사용했습니다.

### 서버 모듈

- 공통 모듈
- [at.favre.lib:bcrypt:0.10.2](https://mvnrepository.com/artifact/at.favre.lib/bcrypt/0.10.2): 비밀번호 해싱을 위해 사용했습니다.
- [com.mysql:mysql-connector-j:9.3.0](https://mvnrepository.com/artifact/com.mysql/mysql-connector-j/9.3.0): MySQL 데이터베이스 연결을 위해 사용했습니다.
- [redis.clients:jedis:6.0.0](https://mvnrepository.com/artifact/redis.clients/jedis/6.0.0): Redis 데이터베이스 연결을 위해 사용했습니다.

### 클라이언트 모듈

- 공통 모듈

## 구현된 기능

세션 기반 유저 인증 서비스를 구현했습니다.

유저를 영속하기 위하여 MySQL을 사용하였고, 세션을 유지하기 위하여 Redis를 사용했습니다.

### MySQL 초기화 스크립트

```sql
CREATE DATABASE IF NOT EXISTS example_user;

USE example_user;

DROP TABLE IF EXISTS user;

CREATE TABLE user
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name     VARCHAR(100) NOT NULL
);

INSERT INTO user (email, password, name)
VALUES ('example@example.com', '(ENCRYPTED)', '테스트 유저');
```

### API 엔드포인트

| 기능             | 메소드 | 경로                    | 요청 바디 필드              | 응답 코드      | 응답 바디 / 헤더                    | 인증 필요 |
| ---------------- | ------ | ----------------------- | --------------------------- | -------------- | ----------------------------------- | --------- |
| 로그인           | POST   | `/auth/sessions`        | `email`, `password`         | 200 OK         | Header: `Set-Cookie: sessionId=...` | X         |
| 로그아웃         | DELETE | `/auth/sessions`        |                             | 204 No Content |                                     | O         |
| 회원가입         | POST   | `/users`                | `email`, `password`, `name` | 201 Created    |                                     | X         |
| 이름 수정        | PATCH  | `/users/me`             | `name`                      | 200 OK         | `id`, `email`, `name`               | O         |
| 사용자 목록 조회 | GET    | `/users`                |                             | 200 OK         | `users`: 사용자 리스트              | O         |
| 내 정보 조회     | GET    | `/users/me`             |                             | 200 OK         | `id`, `email`, `name`               | O         |
| 사용자 조회      | GET    | `/users/other/{userId}` |                             | 200 OK         | `id`, `email`, `name`               | O         |

## 코드 설명

### 공통 모듈

서버와 클라이언트 모듈에서 공통적으로 사용되는 코드입니다.

#### constant

#### HttpMethod

HTTP 메소드의 열거형 클래스입니다.

```java
public enum HttpMethod {

    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE,
    CONNECT,
}
```

#### HttpStatus

HTTP 응답 코드의 열거형 클래스입니다.

```java
@RequiredArgsConstructor
public enum HttpStatus {

    // 1xx Informational Responses
    CONTINUE(100, "Continue"),
    SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    PROCESSING(102, "Processing"),
    EARLY_HINTS(103, "Early Hints"),

    // 2xx Successful Responses
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
    NO_CONTENT(204, "No Content"),
    RESET_CONTENT(205, "Reset Content"),
    PARTIAL_CONTENT(206, "Partial Content"),
    MULTI_STATUS(207, "Multi-Status"),
    ALREADY_REPORTED(208, "Already Reported"),
    IM_USED(209, "IM Used"),

    // 3xx Redirection Messages
    MULTIPLE_CHOICES(300, "Multiple Choices"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"),
    USE_PROXY(305, "Use Proxy"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    PERMANENT_REDIRECT(308, "Permanent Redirect"),

    // 4xx Client Error Responses
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"),
    GONE(410, "Gone"),
    LENGTH_REQUIRED(411, "Length Required"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
    URI_TOO_LONG(414, "URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable"),
    EXPECTATION_FAILED(417, "Expectation Failed"),
    IM_A_TEAPOT(418, "I'm a teapot"),
    MISDIRECTED_REQUEST(421, "Misdirected Request"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    LOCKED(423, "Locked"),
    FAILED_DEPENDENCY(424, "Failed Dependency"),
    TOO_EARLY(425, "Too Early"),
    UPGRADE_REQUIRED(426, "Upgrade Required"),
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),

    // 5xx Server Error Responses
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
    LOOP_DETECTED(508, "Loop Detected"),
    NOT_EXTENDED(510, "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required"),
    ;

    private final int code;
    private final String message;
```

숫자 기반 응답 코드를 HttpStatus로 변환하는 메소드입니다.

```java
    public static HttpStatus fromCode(int code) {

        for (HttpStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown HTTP status code: " + code);
    }
```

HTTP 응답 포맷에 맞게 _(200 OK)_ 포맷하여 String으로 변환합니다.

```java
    @Override
    public String toString() {

        return code + " " + message;
    }
}
```

#### HttpVersion

HTTP의 지원 버전(1.0, 1.1, 2.0, 3.0)의 열거형 클래스입니다. 기능은 위와 동일합니다.

````java
@RequiredArgsConstructor
public enum HttpVersion {

    HTTP_1_0("1.0"),
    HTTP_1_1("1.1"),
    HTTP_2_0("2.0"),
    HTTP_3_0("3.0"),
    ;

    private final String version;

    public static HttpVersion fromVersion(String version) {

        for (HttpVersion httpVersion : values()) {
            if (httpVersion.version.equals(version)) {
                return httpVersion;
            }
        }
        throw new IllegalArgumentException("Unknown HTTP version: " + version);
    }

    @Override
    public String toString() {

        return "HTTP/" + version;
    }
}

#### http

#### HttpRequest

HTTP 요청을 다루기 위한 클래스입니다.

```java
@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class HttpRequest {

    private final HttpMethod method;

    private final HttpVersion version;

    private final String path;

    @Singular
    private final Map<String, Object> headers;

    @Singular
    private final Map<String, Object> bodies;

````

소켓의 InputStreamReader를 받아서 한줄씩 값을 읽으며 HttpRequest 객체를 파싱합니다.

```java
    @SneakyThrows(IOException.class)
    public static HttpRequest fromReader(BufferedReader reader) {

        int contentLength = 0;
        HttpRequestBuilder builder = HttpRequest.builder();

        // Request Line
        {
            String[] parts = waitForRequest(reader).split(" ", 3);

            builder.method(HttpMethod.valueOf(parts[0]))
                    .path(parts[1])
                    .version(HttpVersion.fromVersion(parts[2].split("/")[1]));
        }

        // Header
        {
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {

                String[] parts = line.split(": ", 2);

                if (parts[0].equalsIgnoreCase("Content-Length")) {

                    contentLength = Integer.parseInt(parts[1]);
                }

                builder.header(parts[0], parts[1]);
            }
        }

        // Body
        {
            if (contentLength > 0) {
                char[] chars = new char[contentLength];

                if (reader.read(chars) != contentLength) {

                    throw new RuntimeException("헤더의 Content-Length와 본문 길이가 일치하지 않습니다.");
                }

                builder.bodies(JsonMapper.fromJson(new String(chars)));
            }
        }

        return builder.build();
    }

```

처음 reader로 값이 들어올 때 까지 대기한 후, 첫 라인이 들어온다면, 그 값을 반환합니다.

```java
    private static String waitForRequest(BufferedReader reader) throws IOException {

        String line;

        //noinspection StatementWithEmptyBody
        while ((line = reader.readLine()) == null)
            ;

        return line;
    }

```

클라이언트에서 서버로 요청을 보낼 때 Host 헤더를 추가합니다.

```java
    public HttpRequest addHostHeader(String host, int port) {

        return this.toBuilder().header("Host", host + ':' + port).build();
    }

```

body를 읽어서 Content-Type과 Content-Length 헤더를 추가합니다.

```java
    public HttpRequest addContentHeader() {

        return this.toBuilder()
                .header("Content-Type", "application/json")
                .header("Content-Length", bodies.isEmpty() ? 0 : JsonMapper.toJson(bodies).length())
                .build();
    }

```

HTTP 요청 문자열로 변환합니다.

```java
    public String toHttpString() {

        StringBuilder http = new StringBuilder();

        // Request Line
        http.append(method.toString()).append(" ").append(path).append(" ").append(version.toString()).append("\r\n");

        // Header
        headers.forEach((key, value) -> http.append(key).append(": ").append(value).append("\r\n"));

        http.append("\r\n");

        // Body
        http.append(JsonMapper.toJson(bodies));

        return http.toString();
    }
}
```

#### HttpResponse

HTTP 응답을 다루기 위한 클래스입니다.

메소드 및 기타 기능은 위 HttpRequest와 같습니다.

```java
@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class HttpResponse {

    private final HttpVersion version;

    private final HttpStatus status;

    @Singular
    private final Map<String, Object> headers;

    @Singular
    private final Map<String, Object> bodies;

    @SneakyThrows(IOException.class)
    public static HttpResponse fromReader(BufferedReader reader) {

        int contentLength = 0;
        HttpResponseBuilder builder = HttpResponse.builder();

        // Response Line
        {
            String[] parts = waitForResponse(reader).split(" ", 2);

            builder.version(HttpVersion.fromVersion(parts[0].split("/")[1]))
                    .status(HttpStatus.fromCode(Integer.parseInt(parts[1].split(" ")[0])));
        }

        // Header
        {
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {

                String[] parts = line.split(": ", 2);

                if (parts[0].equalsIgnoreCase("Content-Length")) {

                    contentLength = Integer.parseInt(parts[1]);
                }

                builder.header(parts[0], parts[1]);
            }
        }

        // Body
        {
            if (contentLength > 0) {
                char[] chars = new char[contentLength];

                if (reader.read(chars) == contentLength) {

                    builder.bodies(JsonMapper.fromJson(new String(chars)));
                }
            }
        }

        return builder.build();
    }

    private static String waitForResponse(BufferedReader reader) throws IOException {

        String line;

        //noinspection StatementWithEmptyBody
        while ((line = reader.readLine()) == null)
            ;

        return line;
    }

    public HttpResponse addContentHeader() {

        return this.toBuilder()
                .header("Content-Type", "application/json")
                .header("Content-Length", bodies.isEmpty() ? 0 : JsonMapper.toJson(bodies).length())
                .build();
    }

    public String toHttpString() {

        StringBuilder http = new StringBuilder();

        // Response Line
        http.append(version.toString()).append(" ").append(status.toString()).append("\r\n");

        // Header
        headers.forEach((key, value) -> http.append(key).append(": ").append(value).append("\r\n"));

        http.append("\r\n");

        // Body
        http.append(JsonMapper.toJson(bodies));

        return http.toString();
    }
}
```

### 클라이언트 모듈

세션 기반 유저 인증 서비스를 테스트하기 위한 다양한 시나리오를 서버로 전송한 후 그 결과를 받는 코드입니다.

#### scenario

#### Scenario

시나리오 단일 아이템입니다.

시나리오 제목, 요청 객체, 응답이 온 후 할 액션을 포함합니다.

```java
public record Scenario(

        String title,
        Supplier<HttpRequest> request,
        Consumer<HttpResponse> then
) {

}
```

#### ScenarioStorage

시나리오에서 세션을 유지하기 위한 저장 객체입니다.

```java
@Data
public class ScenarioStorage {

    private String sessionId;
}
```

#### ScenarioList

실제 여러 시나리오들을 List로 가지고 있습니다.

```java
public class ScenarioList {

    private static final ScenarioStorage storage = new ScenarioStorage();

    @Getter
    private static final List<Scenario> scenarios = List.of(

            // 회원가입
            new Scenario(
                    "회원가입 성공 [POST : 201 Created]",
                    () -> HttpRequest.builder()
                            .method(HttpMethod.POST)
                            .version(HttpVersion.HTTP_1_1)
                            .path("/users")
                            .body("email", "sondaehyeon@example.com")
                            .body("password", "p4ssw0rd")
                            .body("name", "손대현")
                            .build()
                            .addHostHeader("localhost", 8080)
                            .addContentHeader(),
                    response -> {
                    }
            ), new Scenario(
                    "회원가입 이메일 중복 [POST : 409 Conflict]",
                    () -> HttpRequest.builder()
                            .method(HttpMethod.POST)
                            .version(HttpVersion.HTTP_1_1)
                            .path("/users")
                            .body("email", "sondaehyeon@example.com")
                            .body("password", "p4ssw0rd")
                            .body("name", "손대현")
                            .build()
                            .addHostHeader("localhost", 8080)
                            .addContentHeader(),
                    response -> {
                    }
            ),

            // 모든 시나리오 및 실행 결과에서 더 자세하게 설명하겠습니다.
    );
}
```

#### EntryPoint

시나리오 목록을 하나씩 순회하며 실제 요청을 날리고, 응답을 확인하는 코드입니다.

```java
public class Client {

    public static void main(String[] args) {

        List<Scenario> scenarios = ScenarioList.getScenarios();

        for (int i = 0; i < scenarios.size(); i++) {

            Scenario scenario = scenarios.get(i);

            HttpResponse response = Requester.request(scenario.request().get());
            scenario.then().accept(response);

            System.out.println(
                    "====================================================================================================");

            System.out.println("#" + String.format("%02d", i + 1) + ". " + scenario.title());

            System.out.println("  - 버전: " + response.getVersion());
            System.out.println("  - 응답: " + response.getStatus());
            System.out.println("  - 헤더: ");
            response.getHeaders().forEach((key, value) -> System.out.println("    - " + key + ": " + value));
            System.out.println("  - 바디:");
            response.getBodies().forEach((key, value) -> System.out.println("    - " + key + ": " + value));

            System.out.println(
                    "====================================================================================================");

            System.out.println();

            if (i < scenarios.size() - 1) {
                LineBreaker.breakLine();
            }
        }
    }
}
```

### 서버 모듈

과거 Spring Boot로 여러 프로젝트를 진행하며 영감을 받아 이와 비슷하게 어노테이션 기반으로 라우팅 테이블을 구축할 수 있도록 구현해봤습니다.

#### router

#### RequestMapiing

메소드에 어노테이션을 기반으로 라우팅을 등록하기 위해 사용됩니다.

HttpMethod와 path를 입력받습니다.

이 때 path는 `/users/{userId}`와 같이 동적 라우팅도 가능합니다.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    HttpMethod method();
    String path();
}
```

#### Route

단일 라우트 객체입니다.

상위 글로벌 라우팅 테이블에서 이 객체로 라우터를 관리합니다.

matches를 통해 상위에서 현재 path와 요청 path를 비교하여 라우팅할 수 있습니다.

```java
@Data
@Builder
public class Route {

    private final HttpMethod method;

    private final String path;

    private final Function<HttpRequest, HttpResponse> handler;

    public boolean matches(HttpMethod requestMethod, String requestPath) {

        return this.method.equals(requestMethod) &&
                Pattern.compile("^" + path.replaceAll("\\{[^}]+}", "([^/]+)") + "$").matcher(requestPath).matches();
    }
}
```

#### Router

위 Route를 글로벌로 테이블화하여 관리합니다.

```java
public class Router {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final List<Route> router = new ArrayList<>();
```

Java Reflection API의 Method를 받아 위 router에 등록합니다.

```java
    private static final Function<Method, Route.RouteBuilder> ROUTE_FUNCTION = (method) -> Route.builder()
            .method(method.getAnnotation(RequestMapping.class).method())
            .path(method.getAnnotation(RequestMapping.class).path())
            .handler((request) -> {

                try {

                    return (HttpResponse) method.invoke(
                            method.getDeclaringClass().getDeclaredConstructor().newInstance(),
                            request.getBodies(),
                            parseVariables(method.getAnnotation(RequestMapping.class).path(), request.getPath()),
                            request.getHeaders()
                    );
                } catch (Throwable e) {

                    Throwable cause = e.getCause();

                    if (cause instanceof ApiException apiException) {
                        throw apiException;
                    }

                    throw new ApiException(cause.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            });
```

글로벌 라우터 테이블을 초기화할 때 사용됩니다.

컨트롤러의 모든 메소드를 불러온 후 올바른 메소드만 필터링하여 router에 등록합니다.

이 때, GET 요청인 경우 HEAD도 같이 등록합니다.

```java
    public static void initialize() {

        Arrays.stream(Controller.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .filter(method -> method.getParameterCount() == 3)
                .filter(method -> method.getParameterTypes()[0].equals(Map.class))
                .filter(method -> method.getParameterTypes()[1].equals(Map.class))
                .filter(method -> method.getParameterTypes()[2].equals(Map.class))
                .filter(method -> method.getReturnType().equals(HttpResponse.class))
                .peek(method -> method.setAccessible(true))
                .flatMap(method -> {

                    List<Route> local = new ArrayList<>(List.of(ROUTE_FUNCTION.apply(method).build()));

                    if (HttpMethod.GET.equals(method.getAnnotation(RequestMapping.class).method())) {

                        local.add(ROUTE_FUNCTION.apply(method).method(HttpMethod.HEAD).build());
                    }

                    return local.stream();
                })
                .peek(router::add)
                .forEachOrdered(route -> System.out.println(
                        "  - " + route.getMethod() + " ".repeat(8 - route.getMethod().name().length()) +
                                route.getPath()));
    }
```

EntryPoint에서 HttpRequest를 받아 이를 적당한 라우터로 전송한 후 응답을 받을 때 사용됩니다.

예시로 HTTP/1.1만 받을 수 있도록 설정하였고, router에서 matches되는 Route를 골라 실제 요청을 처리합니다.

만약 route를 찾을 수 없다면 404를 반환하고, 요청 처리 중 예외가 발생한 경우 적당히 그 값을 반환합니다.

```java
    public static HttpResponse handle(HttpRequest request) {

        if (!request.getVersion().equals(HttpVersion.HTTP_1_1)) {

            return HttpResponse.builder()
                    .version(HttpVersion.HTTP_1_1)
                    .status(HttpStatus.HTTP_VERSION_NOT_SUPPORTED)
                    .build()
                    .addContentHeader();
        }

        try {

            Optional<Route> target = router.stream()
                    .filter(route -> route.matches(request.getMethod(), request.getPath()))
                    .findFirst();

            if (target.isEmpty()) {

                return HttpResponse.builder().version(HttpVersion.HTTP_1_1).status(HttpStatus.NOT_FOUND).build();
            }

            HttpResponse response = target.get().getHandler().apply(request).addContentHeader();

            if (HttpMethod.HEAD.equals(request.getMethod())) {

                return response.toBuilder().clearBodies().build();
            }

            return response;
        } catch (RuntimeException e) {

            if (e instanceof ApiException apiException) {

                return HttpResponse.builder()
                        .version(HttpVersion.HTTP_1_1)
                        .status(apiException.getStatus())
                        .body("message", apiException.getMessage())
                        .build()
                        .addContentHeader();
            }

            return HttpResponse.builder()
                    .version(HttpVersion.HTTP_1_1)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("message", e.getMessage())
                    .build()
                    .addContentHeader();
        }
    }
```

동적 라우팅 path에서 동적 변수 이름을 파싱하는데 사용됩니다.

`/projects/{userId}/{projectId}` 와 같은 URL을 전송하면 `{"userId", "projectId"}`를 반환합니다.

```java
    private static List<String> parseNames(String routerUrl) {

        List<String> names = new ArrayList<>();

        Pattern variablePattern = Pattern.compile("\\{([^}]+)}");
        Matcher matcher = variablePattern.matcher(routerUrl);

        while (matcher.find()) {
            names.add(matcher.group(1));
        }

        return names;
    }
```

위 동적 라우팅의 name을 기반으로 실제 요청에서 그 값을 파싱합니다.

```java
    private static Map<String, String> parseVariables(String routerUrl, String path) {

        Map<String, String> values = new HashMap<>();

        List<String> names = parseNames(routerUrl);
        Matcher matcher = Pattern.compile("^" + routerUrl.replaceAll("\\{[^}]+}", "([^/]+)") + "$").matcher(path);

        if (matcher.matches()) {
            for (int i = 0; i < names.size(); i++) {
                values.put(names.get(i), matcher.group(i + 1));
            }
        }

        return values;
    }
}
```

#### util

#### MySQL

MySQL 쿼리를 다루기 위한 유틸리티 클래스입니다.

```java
public class MySQL {

    private static final String MYSQL_HOST = "localhost";
    private static final int MYSQL_PORT = 3306;
    private static final String MYSQL_NAME = "example_user";
    private static final String MYSQL_USERNAME = "root";
    private static final String MYSQL_PASSWORD = "root";

    private static final Connection connection;

```

처음 객체가 로딩될 때, MySQL Connection을 생성합니다. 이 Connection은 이후 계속 사용됩니다.

```java
    static {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + MYSQL_HOST + ":" + MYSQL_PORT + "/" + MYSQL_NAME,
                    MYSQL_USERNAME,
                    MYSQL_PASSWORD
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initialize() {

        @SuppressWarnings("unused") Connection connection1 = connection;
    }
```

select 쿼리를 전송하고, 그 결과를 반환합니다.

```java
    public static ResultSet select(String sql) throws SQLException {

        return select(
                sql, statement -> {
                }
        );
    }

    public static ResultSet select(String sql, Consumer<PreparedStatement> consumer) throws SQLException {

        @SuppressWarnings("SqlSourceToSinkFlow") PreparedStatement statement = connection.prepareStatement(sql);

        consumer.accept(statement);

        return statement.executeQuery();
    }
```

update 및 insert 쿼리를 전송합니다.

```java
    public static void update(String sql, Consumer<PreparedStatement> consumer) throws SQLException {

        @SuppressWarnings("SqlSourceToSinkFlow") PreparedStatement statement = connection.prepareStatement(sql);

        consumer.accept(statement);

        statement.executeUpdate();
    }
```

`Consumer<PreparedStatement>` 에서 예외 처리를 피하기 위한 유틸리티 메소드입니다.

```java
    @SneakyThrows(SQLException.class)
    public static void setInt(PreparedStatement statement, int index, int value) {

        statement.setInt(index, value);
    }

    @SneakyThrows(SQLException.class)
    public static void setString(PreparedStatement statement, int index, String value) {

        statement.setString(index, value);
    }
}
```

#### Redis

Reids 메모리를 다루기 위한 유틸리티 클래스입니다.

```java
public class Redis {

    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;

    private static final UnifiedJedis jedis = new UnifiedJedis(new HostAndPort(REDIS_HOST, REDIS_PORT));

```

메모리 값을 삽입하는데, TTL을 seconds만큼 부여합니다.

```java
    public static void set(String key, String value, int seconds) {

        jedis.setex(key, seconds, value);
    }

```

메모리에서 값을 읽습니다.

```java
    public static String get(String key) {

        return jedis.get(key);
    }

```

메모리에서 값을 삭제합니다.

```java
    public static void delete(String key) {

        jedis.del(key);
    }

```

메모리에 그 값이 존재하는지 확인합니다.

```java
    public static boolean exists(String key) {

        return jedis.exists(key);
    }
}
```

#### BodyValidator

Controller에서 body 필드를 검증할 때 사용됩니다.

`validate(bodies, "name", "email")` 와 같이 사용되며, 필드가 누락된 경우 예외가 발생합니다.

```java
public class BodyValidator {

    public static void validate(Map<String, Object> body, String... requiredFields) {

        for (String field : requiredFields) {

            if (Objects.isNull(body) || Objects.isNull(body.get(field))) {

                throw new ApiException("%s를 입력해주세요".formatted(field), HttpStatus.BAD_REQUEST);
            }
        }
    }
}
```

### domain

실제 비즈니스 로직 관련 코드입니다.

#### Controller

HTTP 요청을 처리하기 위한 클래스입니다.

실제 비즈니스 로직은 Service에서 처리되며, Controller에서는 인증 헤더 검증 및 body 검증 및 응답 반환을 처리합니다.

위 API 명세서에 따라 구현되었습니다.

```java
public class Controller {

    private final Service service = new Service();

    @RequestMapping(method = HttpMethod.POST, path = "/auth/sessions")
    public HttpResponse login(
            Map<String, Object> bodies,
            Map<String, String> pathVariables,
            Map<String, Object> headers
    ) {

        BodyValidator.validate(bodies, "email", "password");

        String email = (String) bodies.get("email");
        String password = (String) bodies.get("password");

        String sessionId = service.login(email, password);

        return HttpResponse.builder()
                .status(HttpStatus.OK)
                .version(HttpVersion.HTTP_1_1)
                .header("Set-Cookie", "sessionId=" + sessionId + ";")
                .build();
    }

    @RequestMapping(method = HttpMethod.DELETE, path = "/auth/sessions")
    public HttpResponse logout(
            Map<String, Object> bodies,
            Map<String, String> pathVariables,
            Map<String, Object> headers
    ) {

        UUID sessionId = AuthMiddleware.getSessionId(headers);

        service.logout(sessionId);

        return HttpResponse.builder().status(HttpStatus.NO_CONTENT).version(HttpVersion.HTTP_1_1).build();
    }

    @RequestMapping(method = HttpMethod.POST, path = "/users")
    public HttpResponse register(
            Map<String, Object> bodies,
            Map<String, String> pathVariables,
            Map<String, Object> headers
    ) {

        BodyValidator.validate(bodies, "email", "password", "name");

        String email = (String) bodies.get("email");
        String password = (String) bodies.get("password");
        String name = (String) bodies.get("name");

        service.register(email, password, name);

        return HttpResponse.builder().status(HttpStatus.CREATED).version(HttpVersion.HTTP_1_1).build();
    }

    @RequestMapping(method = HttpMethod.PATCH, path = "/users/me")
    public HttpResponse updateName(
            Map<String, Object> bodies,
            Map<String, String> pathVariables,
            Map<String, Object> headers
    ) {

        BodyValidator.validate(bodies, "name");

        User user = AuthMiddleware.getUser(headers);
        String name = (String) bodies.get("name");

        User updatedUser = service.updateName(user, name);

        return HttpResponse.builder()
                .status(HttpStatus.OK)
                .version(HttpVersion.HTTP_1_1)
                .body("id", updatedUser.id())
                .body("email", updatedUser.email())
                .body("name", updatedUser.name())
                .build();
    }

    @RequestMapping(method = HttpMethod.GET, path = "/users")
    public HttpResponse getUserList(
            Map<String, Object> bodies,
            Map<String, String> pathVariables,
            Map<String, Object> headers
    ) {

        AuthMiddleware.getUser(headers);

        List<User> users = service.getUserList();

        return HttpResponse.builder().status(HttpStatus.OK).version(HttpVersion.HTTP_1_1).body("users", users).build();
    }

    @RequestMapping(method = HttpMethod.GET, path = "/users/me")
    public HttpResponse getCurrentUser(
            Map<String, Object> bodies,
            Map<String, String> pathVariables,
            Map<String, Object> headers
    ) {

        User user = AuthMiddleware.getUser(headers);

        return HttpResponse.builder()
                .status(HttpStatus.OK)
                .version(HttpVersion.HTTP_1_1)
                .body("id", user.id())
                .body("email", user.email())
                .body("name", user.name())
                .build();
    }

    @RequestMapping(method = HttpMethod.GET, path = "/users/other/{userId}")
    public HttpResponse getUserById(
            Map<String, Object> bodies,
            Map<String, String> pathVariables,
            Map<String, Object> headers
    ) {

        AuthMiddleware.getUser(headers);

        User user = service.getUserById(Integer.parseInt(pathVariables.get("userId")));

        return HttpResponse.builder()
                .status(HttpStatus.OK)
                .version(HttpVersion.HTTP_1_1)
                .body("id", user.id())
                .body("email", user.email())
                .body("name", user.name())
                .build();
    }
}
```

#### Service

Controller로부터 로직을 위임받아, DB(Repository)와 통신하여 데이터를 처리합니다.

```java
public class Service {

    private final UserRepository userRepository = new UserRepository();
    private final SessionRepository sessionRepository = new SessionRepository();

    public String login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (!BCrypt.verifyer().verify(password.toCharArray(), user.password()).verified) {

            throw new ApiException("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        return sessionRepository.save(user);
    }

    public void logout(UUID sessionId) {

        sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException("세션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        sessionRepository.delete(sessionId);
    }

    public void register(String email, String password, String name) {

        if (userRepository.findByEmail(email).isPresent()) {

            throw new ApiException("이미 존재하는 이메일입니다.", HttpStatus.CONFLICT);
        }

        userRepository.save(User.builder()
                .email(email)
                .password(BCrypt.withDefaults().hashToString(12, password.toCharArray()))
                .name(name)
                .build());
    }

    public User updateName(User user, String name) {

        return userRepository.save(user.toBuilder().name(name).build()).hidePassword();
    }

    public List<User> getUserList() {

        return userRepository.findAll().stream().map(User::hidePassword).toList();
    }

    public User getUserById(int userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }
}
```

#### UserRepository

MySQL의 User 테이블을 다루는 Repository입니다.

실제 Connection을 기반으로 쿼리를 전송합니다.

```java
public class UserRepository {

    @SneakyThrows(SQLException.class)
    public List<User> findAll() {

        try (
                ResultSet count = MySQL.select("SELECT COUNT(*) FROM user");
                ResultSet results = MySQL.select("SELECT * FROM user")
        ) {

            count.next();

            return IntStream.range(0, count.getInt(1))
                    .mapToObj((i) -> results)
                    .map(this::mapToUser)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }
    }

    @SneakyThrows(SQLException.class)
    public Optional<User> findById(int id) {

        try (
                ResultSet results = MySQL.select("SELECT * FROM user WHERE id = ?", ps -> setInt(ps, 1, id))
        ) {

            return mapToUser(results);
        }
    }

    @SneakyThrows(SQLException.class)
    public Optional<User> findByEmail(String email) {

        try (
                ResultSet results = MySQL.select("SELECT * FROM user WHERE email = ?", ps -> setString(ps, 1, email))
        ) {

            return mapToUser(results);
        }
    }

    @SneakyThrows(SQLException.class)
    public User save(User user) {

        Optional<User> userOptional = findById(user.id());

        if (userOptional.isPresent()) {
            MySQL.update(
                    "UPDATE user SET email = ?, password = ?, name = ? WHERE id = ?", ps -> {
                        setString(ps, 1, user.email());
                        setString(ps, 2, user.password());
                        setString(ps, 3, user.name());
                        setInt(ps, 4, user.id());
                    }
            );

            return user;
        } else {
            MySQL.update(
                    "INSERT INTO user (email, password, name) VALUES (?, ?, ?)", ps -> {
                        setString(ps, 1, user.email());
                        setString(ps, 2, user.password());
                        setString(ps, 3, user.name());
                    }
            );

            return findByEmail(user.email()).orElseThrow();
        }
    }

    @SneakyThrows(SQLException.class)
    private Optional<User> mapToUser(ResultSet results) {

        if (!results.next()) {
            return Optional.empty();
        }

        return Optional.of(User.builder()
                .id(results.getInt("id"))
                .email(results.getString("email"))
                .password(results.getString("password"))
                .name(results.getString("name"))
                .build());
    }
}
```

#### SessionRepository

Redis에서 세션을 다루는 Repository입니다.

```java
public class SessionRepository {

    private static final int TTL = 60 * 60 * 24;

    private final UserRepository userRepository = new UserRepository();

    public Optional<User> findById(UUID id) {

        if (Redis.exists(id.toString())) {

            int userId = Integer.parseInt(Redis.get(id.toString()));

            return userRepository.findById(userId);
        } else {

            return Optional.empty();
        }
    }

    public void delete(UUID id) {

        Redis.delete(id.toString());
    }

    public String save(User user) {

        String key = UUID.randomUUID().toString();

        Redis.set(key, Integer.toString(user.id()), TTL);

        return key;
    }
}
```

#### AuthMiddleware

헤더에서 인증이 된 유저인지 확인하고, 실제 유저 객체를 반환합니다.

만약 인증되어있지 않다면 예외를 발생합니다.

```java
public class AuthMiddleware {

    private static final SessionRepository sessionRepository = new SessionRepository();

    public static User getUser(Map<String, Object> headers) {

        return sessionRepository.findById(getSessionId(headers))
                .orElseThrow(() -> new ApiException("세션이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED));
    }

    public static UUID getSessionId(Map<String, Object> headers) {

        String authorization = (String) headers.get("Authorization");

        if (Objects.isNull(authorization) || !authorization.startsWith("Bearer ")) {

            throw new ApiException("인증이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        return UUID.fromString(authorization.substring("Bearer ".length()));
    }
}
```

### EntryPoint

클라이언트로부터 요청을 받고, Router를 통해 요청을 처리한 후 그 결과를 반환합니다.

```java
public class Server {

    private static final int PORT = 8080;

    static {

        MySQL.initialize();

        System.out.println(
                "====================================================================================================");
        System.out.println("Registering routes.");

        Router.initialize();

        System.out.println(
                "====================================================================================================");
        System.out.println();
    }

    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Server is running on port " + PORT + ".\n");

            Socket socket;

            while ((socket = serverSocket.accept()) != null) {

                try (

                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                ) {

                    HttpRequest request = HttpRequest.fromReader(reader);

                    System.out.println(
                            "====================================================================================================");

                    System.out.println("  - 메소드: " + request.getMethod());
                    System.out.println("  - 버전: " + request.getVersion());
                    System.out.println("  - 경로: " + request.getPath());
                    System.out.println("  - 헤더: ");
                    request.getHeaders().forEach((key, value) -> System.out.println("    - " + key + ": " + value));
                    System.out.println("  - 바디:");
                    request.getBodies().forEach((key, value) -> System.out.println("    - " + key + ": " + value));

                    System.out.println(
                            "====================================================================================================");

                    System.out.println();

                    HttpResponse response = Router.handle(request);

                    writer.write(response.toHttpString());
                    writer.flush();
                }
            }
        }
    }
}
```

## 시나리오 목록

### 회원가입 성공

#### 클라이언트 로그 (응답)

```
====================================================================================================
#01. 회원가입 성공 [POST : 201 Created]
  - 버전: HTTP/1.1
  - 응답: 201 Created
  - 헤더:
    - Content-Type: application/json
    - Content-Length: 0
  - 바디:
====================================================================================================
```

```
#### 서버 로그 (요청)
====================================================================================================
  - 메소드: POST
  - 버전: HTTP/1.1
  - 경로: /users
  - 헤더:
    - Host: localhost:8080
    - Content-Type: application/json
    - Content-Length: 70
  - 바디:
    - email: sondaehyeon@example.com
    - password: p4ssw0rd
    - name: 손대현
====================================================================================================
```

#### 와이어샤크

![request](images/wireshark/1a.png)
![response](images/wireshark/1b.png)

#### 데이터베이스

```bash
mysql> select * from user;
+----+-------------------------+--------------------------------------------------------------+------------------+
| id | email                   | password                                                     | name             |
+----+-------------------------+--------------------------------------------------------------+------------------+
|  1 | example@example.com     | (ENCRYPTED)                                                  | 테스트 유저      |
|  2 | sondaehyeon@example.com | $2a$12$GG915FjEg2L22foRt6cv0.oG1Wayv6akfLcw2/c9gx/MjgLtQLk/y | 손대현           |
+----+-------------------------+--------------------------------------------------------------+------------------+
```

### 회원가입 이메일 중복

#### 클라이언트 로그 (응답)

```
====================================================================================================
#02. 회원가입 이메일 중복 [POST : 409 Conflict]
  - 버전: HTTP/1.1
  - 응답: 409 Conflict
  - 헤더:
    - Content-Type: application/json
    - Content-Length: 29
  - 바디:
    - message: 이미 존재하는 이메일입니다.
====================================================================================================
```

#### 서버 로그 (요청)

```
====================================================================================================
  - 메소드: POST
  - 버전: HTTP/1.1
  - 경로: /users
  - 헤더:
    - Host: localhost:8080
    - Content-Type: application/json
    - Content-Length: 70
  - 바디:
    - email: sondaehyeon@example.com
    - password: p4ssw0rd
    - name: 손대현
====================================================================================================
```

#### 와이어샤크

![request](images/wireshark/2a.png)
![response](images/wireshark/2b.png)

### 로그인 성공

#### 클라이언트 로그 (응답)

```
====================================================================================================
#03. 로그인 성공 [POST : 200 OK]
  - 버전: HTTP/1.1
  - 응답: 200 OK
  - 헤더:
    - Set-Cookie: sessionId=f2d269fb-d5c1-492d-9756-13b23cd225ae;
    - Content-Type: application/json
    - Content-Length: 0
  - 바디:
====================================================================================================
```

#### 서버 로그 (요청)

```
====================================================================================================
  - 메소드: POST
  - 버전: HTTP/1.1
  - 경로: /auth/sessions
  - 헤더:
    - Host: localhost:8080
    - Content-Type: application/json
    - Content-Length: 57
  - 바디:
    - email: sondaehyeon@example.com
    - password: p4ssw0rd
====================================================================================================
```

#### 와이어샤크

![request](images/wireshark/3a.png)
![response](images/wireshark/3b.png)

#### 데이터베이스

```bash
127.0.0.1:6379> keys *
1) "f2d269fb-d5c1-492d-9756-13b23cd225ae"
127.0.0.1:6379> get f2d269fb-d5c1-492d-9756-13b23cd225ae
"2"
```

### 로그인 실패

#### 클라이언트 로그 (응답)

```
====================================================================================================
#04. 로그인 실패 [POST : 401 Unauthorized]
  - 버전: HTTP/1.1
  - 응답: 401 Unauthorized
  - 헤더:
    - Content-Type: application/json
    - Content-Length: 30
  - 바디:
    - message: 비밀번호가 일치하지 않습니다.
====================================================================================================
```

#### 서버 로그 (요청)

```
====================================================================================================
  - 메소드: POST
  - 버전: HTTP/1.1
  - 경로: /auth/sessions
  - 헤더:
    - Host: localhost:8080
    - Content-Type: application/json
    - Content-Length: 63
  - 바디:
    - email: sondaehyeon@example.com
    - password: wrong_p4ssw0rd
====================================================================================================
```

#### 와이어샤크

![request](images/wireshark/4a.png)
![response](images/wireshark/4b.png)

### 로그아웃 성공

#### 클라이언트 로그 (응답)

```
====================================================================================================
#05. 로그아웃 성공 [DELETE : 204 No Content]
  - 버전: HTTP/1.1
  - 응답: 204 No Content
  - 헤더:
    - Content-Type: application/json
    - Content-Length: 0
  - 바디:
====================================================================================================
```

#### 서버 로그 (요청)

```
====================================================================================================
  - 메소드: DELETE
  - 버전: HTTP/1.1
  - 경로: /auth/sessions
  - 헤더:
    - Authorization: Bearer f2d269fb-d5c1-492d-9756-13b23cd225ae
    - Host: localhost:8080
  - 바디:
====================================================================================================
```

#### 와이어샤크

![request](images/wireshark/5a.png)
![response](images/wireshark/5b.png)

#### 데이터베이스

```bash
127.0.0.1:6379> keys *
(empty array)
```

### 재로그인 성공

#### 클라이언트 로그 (응답)

```
====================================================================================================
#06. 재로그인 성공 [POST : 200 OK]
  - 버전: HTTP/1.1
  - 응답: 200 OK
  - 헤더:
    - Set-Cookie: sessionId=d89b7cb9-a2f9-4cf2-a789-23a214bf3455;
    - Content-Type: application/json
    - Content-Length: 0
  - 바디:
====================================================================================================
```

#### 서버 로그 (요청)

```
====================================================================================================
  - 메소드: POST
  - 버전: HTTP/1.1
  - 경로: /auth/sessions
  - 헤더:
    - Host: localhost:8080
    - Content-Type: application/json
    - Content-Length: 57
  - 바디:
    - email: sondaehyeon@example.com
    - password: p4ssw0rd
====================================================================================================
```

#### 와이어샤크

![request](images/wireshark/6a.png)
![response](images/wireshark/6b.png)

#### 데이터베이스

```bash
127.0.0.1:6379> keys *
1) "d89b7cb9-a2f9-4cf2-a789-23a214bf3455"
127.0.0.1:6379> get d89b7cb9-a2f9-4cf2-a789-23a214bf3455
"2"
```

### 이름 수정 성공

#### 클라이언트 로그 (응답)

```
====================================================================================================
#07. 이름 수정 성공 [PATCH : 200 OK]
  - 버전: HTTP/1.1
  - 응답: 200 OK
  - 헤더:
    - Content-Type: application/json
    - Content-Length: 55
  - 바디:
    - id: 2
    - email: sondaehyeon@example.com
    - name: 새이름
====================================================================================================
```

#### 서버 로그 (요청)

```
====================================================================================================
  - 메소드: PATCH
  - 버전: HTTP/1.1
  - 경로: /users/me
  - 헤더:
    - Authorization: Bearer d89b7cb9-a2f9-4cf2-a789-23a214bf3455
    - Host: localhost:8080
    - Content-Type: application/json
    - Content-Length: 14
  - 바디:
    - name: 새이름
====================================================================================================
```

#### 와이어샤크

![request](images/wireshark/7a.png)
![response](images/wireshark/7b.png)

#### 데이터베이스

```bash
mysql> select * from user;
+----+-------------------------+--------------------------------------------------------------+------------------+
| id | email                   | password                                                     | name             |
+----+-------------------------+--------------------------------------------------------------+------------------+
|  1 | example@example.com     | (ENCRYPTED)                                                  | 테스트 유저      |
|  2 | sondaehyeon@example.com | $2a$12$GG915FjEg2L22foRt6cv0.oG1Wayv6akfLcw2/c9gx/MjgLtQLk/y | 새이름           |
+----+-------------------------+--------------------------------------------------------------+------------------+
```

### 전체 사용자 목록 조회

#### 클라이언트 로그 (응답)

```
====================================================================================================
#08. 전체 사용자 목록 조회 [GET : 200 OK]
  - 버전: HTTP/1.1
  - 응답: 200 OK
  - 헤더:
    - Content-Type: application/json
    - Content-Length: 154
  - 바디:
    - users: [{id=1, email=example@example.com, password=null, name=테스트 유저}, {id=2, email=sondaehyeon@example.com, password=null, name=새이름}]
====================================================================================================
```

#### 서버 로그 (요청)

```
====================================================================================================
  - 메소드: GET
  - 버전: HTTP/1.1
  - 경로: /users
  - 헤더:
    - Authorization: Bearer d89b7cb9-a2f9-4cf2-a789-23a214bf3455
    - Host: localhost:8080
  - 바디:
====================================================================================================
```

#### 와이어샤크

![request](images/wireshark/8a.png)
![response](images/wireshark/8b.png)

### HEAD 요청 - 사용자 목록

#### 클라이언트 로그 (응답)

```
====================================================================================================
#09. HEAD 요청 - 사용자 목록 [HEAD : 200 OK]
  - 버전: HTTP/1.1
  - 응답: 200 OK
  - 헤더:
    - Content-Type: application/json
    - Content-Length: 154
  - 바디:
====================================================================================================
```

#### 서버 로그 (요청)

```
====================================================================================================
  - 메소드: HEAD
  - 버전: HTTP/1.1
  - 경로: /users
  - 헤더:
    - Authorization: Bearer d89b7cb9-a2f9-4cf2-a789-23a214bf3455
    - Host: localhost:8080
  - 바디:
====================================================================================================
```

#### 와이어샤크

![request](images/wireshark/9a.png)
![response](images/wireshark/9b.png)

### 로그인되지 않음

#### 클라이언트 로그 (응답)

```
====================================================================================================
#10. 로그인되지 않음 [GET : 401 Unauthorized]
  - 버전: HTTP/1.1
  - 응답: 401 Unauthorized
  - 헤더:
    - Content-Type: application/json
    - Content-Length: 24
  - 바디:
    - message: 인증이 필요합니다.
====================================================================================================
```

#### 서버 로그 (요청)

```
====================================================================================================
  - 메소드: GET
  - 버전: HTTP/1.1
  - 경로: /users
  - 헤더:
    - Host: localhost:8080
  - 바디:
====================================================================================================
```

#### 와이어샤크

![request](images/wireshark/10a.png)
![response](images/wireshark/10b.png)

### 내 정보 조회

#### 클라이언트 로그 (응답)

```
====================================================================================================
#11. 내 정보 조회 [GET : 200 OK]
  - 버전: HTTP/1.1
  - 응답: 200 OK
  - 헤더:
    - Content-Type: application/json
    - Content-Length: 55
  - 바디:
    - id: 2
    - email: sondaehyeon@example.com
    - name: 새이름
====================================================================================================
```

#### 서버 로그 (요청)

```
====================================================================================================
  - 메소드: GET
  - 버전: HTTP/1.1
  - 경로: /users/me
  - 헤더:
    - Authorization: Bearer d89b7cb9-a2f9-4cf2-a789-23a214bf3455
    - Host: localhost:8080
  - 바디:
====================================================================================================
```

#### 와이어샤크

![request](images/wireshark/11a.png)
![response](images/wireshark/11b.png)

### 사용자 ID 조회 성공

#### 클라이언트 로그 (응답)

```
====================================================================================================
#12. 사용자 ID 조회 성공 [GET : 200 OK]
  - 버전: HTTP/1.1
  - 응답: 200 OK
  - 헤더:
    - Content-Type: application/json
    - Content-Length: 54
  - 바디:
    - id: 1
    - email: example@example.com
    - name: 테스트 유저
====================================================================================================
```

#### 서버 로그 (요청)

```
====================================================================================================
  - 메소드: GET
  - 버전: HTTP/1.1
  - 경로: /users/other/1
  - 헤더:
    - Authorization: Bearer d89b7cb9-a2f9-4cf2-a789-23a214bf3455
    - Host: localhost:8080
  - 바디:
====================================================================================================
```

#### 와이어샤크

![request](images/wireshark/12a.png)
![response](images/wireshark/12b.png)

### 사용자 없음

#### 클라이언트 로그 (응답)

```
====================================================================================================
#13. 사용자 없음 [GET : 404 Not Found]
  - 버전: HTTP/1.1
  - 응답: 404 Not Found
  - 헤더:
    - Content-Type: application/json
    - Content-Length: 28
  - 바디:
    - message: 유저를 찾을 수 없습니다.
====================================================================================================
```

#### 서버 로그 (요청)

```
====================================================================================================
  - 메소드: GET
  - 버전: HTTP/1.1
  - 경로: /users/other/99999
  - 헤더:
    - Authorization: Bearer d89b7cb9-a2f9-4cf2-a789-23a214bf3455
    - Host: localhost:8080
  - 바디:
====================================================================================================
```

#### 와이어샤크

![request](images/wireshark/13a.png)
![response](images/wireshark/13b.png)
