package com.github.son_daehyeon.computer_network_socket.client.scenario;

import com.github.son_daehyeon.common.http.HttpRequest;
import com.github.son_daehyeon.common.http.HttpResponse;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 시나리오를 정의하는 레코드 클래스
 *
 * @param title   시나리오 제목
 * @param request HTTP 요청을 생성하는 공급자
 * @param then    HTTP 응답을 처리하는 소비자
 */
public record Scenario(

        String title,
        Supplier<HttpRequest> request,
        Consumer<HttpResponse> then
) {

}