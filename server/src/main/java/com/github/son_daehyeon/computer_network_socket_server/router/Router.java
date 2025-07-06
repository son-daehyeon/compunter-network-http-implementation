package com.github.son_daehyeon.computer_network_socket_server.router;


import com.github.son_daehyeon.common.constant.HttpMethod;
import com.github.son_daehyeon.common.constant.HttpStatus;
import com.github.son_daehyeon.common.constant.HttpVersion;
import com.github.son_daehyeon.common.http.HttpRequest;
import com.github.son_daehyeon.common.http.HttpResponse;
import com.github.son_daehyeon.computer_network_socket_server.domain.controller.Controller;
import com.github.son_daehyeon.computer_network_socket_server.util.ApiException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 단일 HTTP 라우터를 관리하는 클래스
 */
public class Router {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final List<Route> router = new ArrayList<>();

    /**
     * HTTP 요청 메소드와 경로를 기반으로 라우트를 생성하는 함수
     */
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

    /**
     * 모든 Controller의 메소드를 조회하여 라우터에 등록
     */
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

    /**
     * URL에 해당되는 라우터로 요청을 처리하는 메소드
     *
     * @param request 요청 객체
     *
     * @return HTTP 응답 객체
     */
    public static HttpResponse handle(HttpRequest request) {

        // HTTP 버전이 1.1이 아닌 경우 예외 처리
        if (!request.getVersion().equals(HttpVersion.HTTP_1_1)) {

            return HttpResponse.builder()
                    .version(HttpVersion.HTTP_1_1)
                    .status(HttpStatus.HTTP_VERSION_NOT_SUPPORTED)
                    .build()
                    .addContentHeader();
        }

        try {

            // 요청 메소드와 경로에 맞는 라우트를 찾음
            Optional<Route> target = router.stream()
                    .filter(route -> route.matches(request.getMethod(), request.getPath()))
                    .findFirst();

            // 해당 라우트가 없으면 404 Not Found 응답 반환
            if (target.isEmpty()) {

                return HttpResponse.builder().version(HttpVersion.HTTP_1_1).status(HttpStatus.NOT_FOUND).build();
            }

            // 라우트의 핸들러를 호출하여 요청 처리
            HttpResponse response = target.get().getHandler().apply(request).addContentHeader();

            // HEAD 메소드인 경우 바디를 제거
            if (HttpMethod.HEAD.equals(request.getMethod())) {

                return response.toBuilder().clearBodies().build();
            }

            return response;
        } catch (RuntimeException e) {

            // ApiException이 발생한 경우, 해당 예외의 상태 코드와 메시지를 사용하여 응답 생성
            if (e instanceof ApiException apiException) {

                return HttpResponse.builder()
                        .version(HttpVersion.HTTP_1_1)
                        .status(apiException.getStatus())
                        .body("message", apiException.getMessage())
                        .build()
                        .addContentHeader();
            }

            // 그 외의 예외는 500 Internal Server Error 응답 반환
            return HttpResponse.builder()
                    .version(HttpVersion.HTTP_1_1)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("message", e.getMessage())
                    .build()
                    .addContentHeader();
        }
    }

    /**
     * 라우터 URL에서 변수 이름을 추출하는 메소드
     *
     * @param routerUrl 라우터 URL
     *
     * @return 변수 이름 목록
     */
    private static List<String> parseNames(String routerUrl) {

        List<String> names = new ArrayList<>();

        Pattern variablePattern = Pattern.compile("\\{([^}]+)}");
        Matcher matcher = variablePattern.matcher(routerUrl);

        while (matcher.find()) {
            names.add(matcher.group(1));
        }

        return names;
    }

    /**
     * 라우터 URL과 경로를 기반으로 변수 값을 추출하는 메소드
     *
     * @param routerUrl 라우터 URL
     * @param path      요청 경로
     *
     * @return 변수 이름과 값의 맵
     */
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