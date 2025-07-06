package com.github.son_daehyeon.computer_network_socket.client.scenario;

import com.github.son_daehyeon.common.constant.HttpMethod;
import com.github.son_daehyeon.common.constant.HttpVersion;
import com.github.son_daehyeon.common.http.HttpRequest;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 시나리오 목록을 관리하는 클래스
 */
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

            // 로그인
            new Scenario(
                    "로그인 성공 [POST : 200 OK]",
                    () -> HttpRequest.builder()
                            .method(HttpMethod.POST)
                            .version(HttpVersion.HTTP_1_1)
                            .path("/auth/sessions")
                            .body("email", "sondaehyeon@example.com")
                            .body("password", "p4ssw0rd")
                            .build()
                            .addHostHeader("localhost", 8080)
                            .addContentHeader(),
                    response -> {

                        String sessionId = Arrays.stream(((String) response.getHeaders().get("Set-Cookie")).split(";"))
                                .map(s -> s.split("="))
                                .map(s -> Map.entry(s[0], s[1]))
                                .filter(e -> e.getKey().equals("sessionId"))
                                .findFirst()
                                .orElseThrow()
                                .getValue();

                        storage.setSessionId(sessionId);
                    }
            ), new Scenario(
                    "로그인 실패 [POST : 401 Unauthorized]",
                    () -> HttpRequest.builder()
                            .method(HttpMethod.POST)
                            .version(HttpVersion.HTTP_1_1)
                            .path("/auth/sessions")
                            .body("email", "sondaehyeon@example.com")
                            .body("password", "wrong_p4ssw0rd")
                            .build()
                            .addHostHeader("localhost", 8080)
                            .addContentHeader(),
                    response -> {
                    }
            ),

            // 로그아웃
            new Scenario(
                    "로그아웃 성공 [DELETE : 204 No Content]",
                    () -> HttpRequest.builder()
                            .method(HttpMethod.DELETE)
                            .version(HttpVersion.HTTP_1_1)
                            .path("/auth/sessions")
                            .header("Authorization", "Bearer " + storage.getSessionId())
                            .build()
                            .addHostHeader("localhost", 8080),
                    response -> storage.setSessionId(null)
            ),

            // 재로그인
            new Scenario(
                    "재로그인 성공 [POST : 200 OK]",
                    () -> HttpRequest.builder()
                            .method(HttpMethod.POST)
                            .version(HttpVersion.HTTP_1_1)
                            .path("/auth/sessions")
                            .body("email", "sondaehyeon@example.com")
                            .body("password", "p4ssw0rd")
                            .build()
                            .addHostHeader("localhost", 8080)
                            .addContentHeader(),
                    response -> {

                        String sessionId = Arrays.stream(((String) response.getHeaders().get("Set-Cookie")).split(";"))
                                .map(s -> s.split("="))
                                .map(s -> Map.entry(s[0], s[1]))
                                .filter(e -> e.getKey().equals("sessionId"))
                                .findFirst()
                                .orElseThrow()
                                .getValue();

                        storage.setSessionId(sessionId);
                    }
            ),

            // 사용자 이름 수정
            new Scenario(
                    "이름 수정 성공 [PATCH : 200 OK]",
                    () -> HttpRequest.builder()
                            .method(HttpMethod.PATCH)
                            .version(HttpVersion.HTTP_1_1)
                            .path("/users/me")
                            .header("Authorization", "Bearer " + storage.getSessionId())
                            .body("name", "새이름")
                            .build()
                            .addHostHeader("localhost", 8080)
                            .addContentHeader(),
                    response -> {
                    }
            ),

            // 사용자 목록
            new Scenario(
                    "전체 사용자 목록 조회 [GET : 200 OK]",
                    () -> HttpRequest.builder()
                            .method(HttpMethod.GET)
                            .version(HttpVersion.HTTP_1_1)
                            .path("/users")
                            .header("Authorization", "Bearer " + storage.getSessionId())
                            .build()
                            .addHostHeader("localhost", 8080),
                    response -> {
                    }
            ), new Scenario(
                    "HEAD 요청 - 사용자 목록 [HEAD : 200 OK]",
                    () -> HttpRequest.builder()
                            .method(HttpMethod.HEAD)
                            .version(HttpVersion.HTTP_1_1)
                            .path("/users")
                            .header("Authorization", "Bearer " + storage.getSessionId())
                            .build()
                            .addHostHeader("localhost", 8080),
                    response -> {
                    }
            ), new Scenario(
                    "로그인되지 않음 [GET : 401 Unauthorized]",
                    () -> HttpRequest.builder()
                            .method(HttpMethod.GET)
                            .version(HttpVersion.HTTP_1_1)
                            .path("/users")
                            .build()
                            .addHostHeader("localhost", 8080),
                    response -> {
                    }
            ),

            // 내 정보 조회
            new Scenario(
                    "내 정보 조회 [GET : 200 OK]",
                    () -> HttpRequest.builder()
                            .method(HttpMethod.GET)
                            .version(HttpVersion.HTTP_1_1)
                            .path("/users/me")
                            .header("Authorization", "Bearer " + storage.getSessionId())
                            .build()
                            .addHostHeader("localhost", 8080),
                    response -> {
                    }
            ),

            // 사용자 ID로 조회
            new Scenario(
                    "사용자 ID 조회 성공 [GET : 200 OK]",
                    () -> HttpRequest.builder()
                            .method(HttpMethod.GET)
                            .version(HttpVersion.HTTP_1_1)
                            .path("/users/other/1")
                            .header("Authorization", "Bearer " + storage.getSessionId())
                            .build()
                            .addHostHeader("localhost", 8080),
                    response -> {
                    }
            ), new Scenario(
                    "사용자 없음 [GET : 404 Not Found]",
                    () -> HttpRequest.builder()
                            .method(HttpMethod.GET)
                            .version(HttpVersion.HTTP_1_1)
                            .path("/users/other/99999")
                            .header("Authorization", "Bearer " + storage.getSessionId())
                            .build()
                            .addHostHeader("localhost", 8080),
                    response -> {
                    }
            )
    );
}
