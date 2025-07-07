package com.github.son_daehyeon.computer_network_socket.server.util;

import com.github.son_daehyeon.common.constant.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * API 예외를 처리하기 위한 클래스
 */
@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException {

    private final String message;
    private final HttpStatus status;
}