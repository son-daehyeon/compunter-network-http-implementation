package com.github.son_daehyeon.computer_network_socket.server.domain.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.github.son_daehyeon.common.constant.HttpStatus;
import com.github.son_daehyeon.computer_network_socket.server.domain.repository.SessionRepository;
import com.github.son_daehyeon.computer_network_socket.server.domain.repository.UserRepository;
import com.github.son_daehyeon.computer_network_socket.server.domain.schema.User;
import com.github.son_daehyeon.computer_network_socket.server.util.ApiException;

import java.util.List;
import java.util.UUID;

/**
 * 서비스 클래스
 */
public class Service {

    private final UserRepository userRepository = new UserRepository();
    private final SessionRepository sessionRepository = new SessionRepository();

    /**
     * 로그인 메소드
     *
     * @param email    유저 이메일
     * @param password 유저 비밀번호
     *
     * @return 세션 ID
     */
    public String login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (!BCrypt.verifyer().verify(password.toCharArray(), user.password()).verified) {

            throw new ApiException("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        return sessionRepository.save(user);
    }

    /**
     * 로그아웃 메소드
     *
     * @param sessionId 세션 ID
     */
    public void logout(UUID sessionId) {

        sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException("세션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        sessionRepository.delete(sessionId);
    }

    /**
     * 회원가입 메소드
     *
     * @param email    유저 이메일
     * @param password 유저 비밀번호
     * @param name     유저 이름
     */
    public void register(String email, String password, String name) {

        if (userRepository.findByEmail(email).isPresent()) {

            throw new ApiException("이미 존재하는 이메일입니다.", HttpStatus.CONFLICT);
        }

        userRepository.save(User.builder()
                .email(email)
                .password(BCrypt.withDefaults().hashToString(12, password.toCharArray()))
                .name(name)
                .build());
    }

    /**
     * 유저 이름을 업데이트하는 메소드
     *
     * @param user 유저 객체
     * @param name 새로운 이름
     *
     * @return 업데이트된 유저 객체
     */
    public User updateName(User user, String name) {

        return userRepository.save(user.toBuilder().name(name).build()).hidePassword();
    }

    /**
     * 유저 비밀번호를 업데이트하는 메소드
     *
     * @return 업데이트된 유저 객체
     */
    public List<User> getUserList() {

        return userRepository.findAll().stream().map(User::hidePassword).toList();
    }

    /**
     * 유저 ID로 유저를 찾는 메소드
     *
     * @param userId 유저 ID
     *
     * @return 유저 객체
     */
    public User getUserById(int userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }
}