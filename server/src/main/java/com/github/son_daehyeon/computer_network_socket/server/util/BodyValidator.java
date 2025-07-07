package com.github.son_daehyeon.computer_network_socket.server.util;

import com.github.son_daehyeon.common.constant.HttpStatus;

import java.util.Map;
import java.util.Objects;

/**
 * 요청 Body에서 필드 검증을 위한 유틸리티 클래스
 */
public class BodyValidator {

    public static void validate(Map<String, Object> body, String... requiredFields) {

        for (String field : requiredFields) {

            if (Objects.isNull(body) || Objects.isNull(body.get(field))) {

                throw new ApiException("%s를 입력해주세요".formatted(field), HttpStatus.BAD_REQUEST);
            }
        }
    }
}