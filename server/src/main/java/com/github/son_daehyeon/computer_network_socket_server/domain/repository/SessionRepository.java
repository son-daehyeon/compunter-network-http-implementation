package com.github.son_daehyeon.computer_network_socket_server.domain.repository;

import com.github.son_daehyeon.computer_network_socket_server.domain.schema.User;
import com.github.son_daehyeon.computer_network_socket_server.util.Redis;

import java.util.Optional;
import java.util.UUID;

/**
 * 세션 정보를 관리하는 레포지토리 클래스
 */
public class SessionRepository {

    private static final int TTL = 60 * 60 * 24; // 1일

    private final UserRepository userRepository = new UserRepository();

    /**
     * 세션 ID로 유저 정보를 조회합니다.
     *
     * @param id 세션 ID
     *
     * @return Optional<User> 유저 정보가 존재하면 해당 유저, 없으면 빈 Optional
     */
    public Optional<User> findById(UUID id) {

        if (Redis.exists(id.toString())) {

            int userId = Integer.parseInt(Redis.get(id.toString()));

            return userRepository.findById(userId);
        } else {

            return Optional.empty();
        }
    }

    /**
     * 세션 ID로 유저 정보를 삭제합니다.
     *
     * @param id 세션 ID
     */
    public void delete(UUID id) {

        Redis.delete(id.toString());
    }

    /**
     * 유저 정보를 저장하고 세션 ID를 반환합니다.
     *
     * @param user 유저 정보
     *
     * @return 세션 ID
     */
    public String save(User user) {

        String key = UUID.randomUUID().toString();

        Redis.set(key, Integer.toString(user.id()), TTL);

        return key;
    }
}
