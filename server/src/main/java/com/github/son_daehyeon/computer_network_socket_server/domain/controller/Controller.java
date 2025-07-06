package com.github.son_daehyeon.computer_network_socket_server.domain.controller;

import com.github.son_daehyeon.common.constant.HttpMethod;
import com.github.son_daehyeon.common.constant.HttpStatus;
import com.github.son_daehyeon.common.constant.HttpVersion;
import com.github.son_daehyeon.common.http.HttpResponse;
import com.github.son_daehyeon.computer_network_socket_server.domain.middleware.AuthMiddleware;
import com.github.son_daehyeon.computer_network_socket_server.domain.schema.User;
import com.github.son_daehyeon.computer_network_socket_server.domain.service.Service;
import com.github.son_daehyeon.computer_network_socket_server.router.RequestMapping;
import com.github.son_daehyeon.computer_network_socket_server.util.BodyValidator;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * HTTP 요청을 처리하는 컨트롤러 클래스
 */
public class Controller {

    private final Service service = new Service();

    /**
     * HTTP 요청을 처리하는 메소드
     *
     * @param bodies        요청 본문
     * @param pathVariables 경로 변수
     * @param headers       요청 헤더
     *
     * @return HttpResponse HTTP 응답 객체
     */
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

    /**
     * 로그아웃 요청을 처리하는 메소드
     *
     * @param bodies        요청 본문
     * @param pathVariables 경로 변수
     * @param headers       요청 헤더
     *
     * @return HttpResponse HTTP 응답 객체
     */
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

    /**
     * 회원가입 요청을 처리하는 메소드
     *
     * @param bodies        요청 본문
     * @param pathVariables 경로 변수
     * @param headers       요청 헤더
     *
     * @return HttpResponse HTTP 응답 객체
     */
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

    /**
     * 사용자 이름을 업데이트하는 메소드
     *
     * @param bodies        요청 본문
     * @param pathVariables 경로 변수
     * @param headers       요청 헤더
     *
     * @return HttpResponse HTTP 응답 객체
     */
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

    /**
     * 사용자 비밀번호를 업데이트하는 메소드
     *
     * @param bodies        요청 본문
     * @param pathVariables 경로 변수
     * @param headers       요청 헤더
     *
     * @return HttpResponse HTTP 응답 객체
     */
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

    /**
     * 사용자 비밀번호를 업데이트하는 메소드
     *
     * @param bodies        요청 본문
     * @param pathVariables 경로 변수
     * @param headers       요청 헤더
     *
     * @return HttpResponse HTTP 응답 객체
     */
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

    /**
     * 사용자 ID로 사용자를 조회하는 메소드
     *
     * @param bodies        요청 본문
     * @param pathVariables 경로 변수
     * @param headers       요청 헤더
     *
     * @return HttpResponse HTTP 응답 객체
     */
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