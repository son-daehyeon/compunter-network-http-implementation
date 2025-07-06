package com.github.son_daehyeon.computer_network_socket_server.domain.middleware;

import com.github.son_daehyeon.common.constant.HttpStatus;
import com.github.son_daehyeon.computer_network_socket_server.domain.repository.SessionRepository;
import com.github.son_daehyeon.computer_network_socket_server.domain.schema.User;
import com.github.son_daehyeon.computer_network_socket_server.util.ApiException;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 인증 미들웨어 클래스
 */
public class AuthMiddleware {

    private static final SessionRepository sessionRepository = new SessionRepository();

    /**
     * 헤더에서 세션 ID를 추출하여 해당 세션의 유저 정보를 반환합니다.
     *
     * @param headers 요청 헤더
     *
     * @return User 유저 정보
     */
    public static User getUser(Map<String, Object> headers) {

        return sessionRepository.findById(getSessionId(headers))
                .orElseThrow(() -> new ApiException("세션이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED));
    }

    /**
     * 헤더에서 세션 ID를 추출합니다.
     *
     * @param headers 요청 헤더
     *
     * @return UUID 세션 ID
     */
    public static UUID getSessionId(Map<String, Object> headers) {

        String authorization = (String) headers.get("Authorization");

        if (Objects.isNull(authorization) || !authorization.startsWith("Bearer ")) {

            throw new ApiException("인증이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        return UUID.fromString(authorization.substring("Bearer ".length()));
    }
}