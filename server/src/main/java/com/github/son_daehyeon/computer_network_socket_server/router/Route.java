package com.github.son_daehyeon.computer_network_socket_server.router;

import com.github.son_daehyeon.common.constant.HttpMethod;
import com.github.son_daehyeon.common.http.HttpRequest;
import com.github.son_daehyeon.common.http.HttpResponse;
import lombok.Builder;
import lombok.Data;

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 단일 HTTP 요청을 처리하는 라우터 클래스
 */
@Data
@Builder
public class Route {

    private final HttpMethod method;

    private final String path;

    private final Function<HttpRequest, HttpResponse> handler;

    /**
     * HTTP 요청 메소드와 경로를 기반으로 라우트가 일치하는지 확인합니다.
     *
     * @param requestMethod HTTP 요청 메소드
     * @param requestPath   HTTP 요청 경로
     *
     * @return 일치 여부
     */
    public boolean matches(HttpMethod requestMethod, String requestPath) {

        return this.method.equals(requestMethod) &&
                Pattern.compile("^" + path.replaceAll("\\{[^}]+}", "([^/]+)") + "$").matcher(requestPath).matches();
    }
}