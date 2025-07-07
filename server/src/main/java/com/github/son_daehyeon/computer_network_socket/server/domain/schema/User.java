package com.github.son_daehyeon.computer_network_socket.server.domain.schema;

import lombok.Builder;

@Builder(toBuilder = true)
public record User(

        int id,
        String email,
        String password,
        String name
) {

    /**
     * 비밀번호를 숨긴 User 객체를 반환합니다.
     *
     * @return 비밀번호가 숨겨진 User 객체
     */
    public User hidePassword() {

        return this.toBuilder().password(null).build();
    }
}