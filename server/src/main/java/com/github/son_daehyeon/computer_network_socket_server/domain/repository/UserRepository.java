package com.github.son_daehyeon.computer_network_socket_server.domain.repository;

import com.github.son_daehyeon.computer_network_socket_server.domain.schema.User;
import com.github.son_daehyeon.computer_network_socket_server.util.MySQL;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.github.son_daehyeon.computer_network_socket_server.util.MySQL.setInt;
import static com.github.son_daehyeon.computer_network_socket_server.util.MySQL.setString;

/**
 * 사용자 정보를 관리하는 레포지토리 클래스
 */
public class UserRepository {

    /**
     * 모든 사용자 정보를 조회합니다.
     *
     * @return 사용자 정보 리스트
     */
    @SneakyThrows(SQLException.class)
    public List<User> findAll() {

        try (
                ResultSet count = MySQL.select("SELECT COUNT(*) FROM user");
                ResultSet results = MySQL.select("SELECT * FROM user")
        ) {

            count.next();

            return IntStream.range(0, count.getInt(1))
                    .mapToObj((i) -> results)
                    .map(this::mapToUser)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }
    }

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     *
     * @param id 사용자 ID
     *
     * @return 사용자 정보
     */
    @SneakyThrows(SQLException.class)
    public Optional<User> findById(int id) {

        try (
                ResultSet results = MySQL.select("SELECT * FROM user WHERE id = ?", ps -> setInt(ps, 1, id))
        ) {

            return mapToUser(results);
        }
    }

    /**
     * 사용자 이메일로 사용자 정보를 조회합니다.
     *
     * @param email 사용자 이메일
     *
     * @return 사용자 정보
     */
    @SneakyThrows(SQLException.class)
    public Optional<User> findByEmail(String email) {

        try (
                ResultSet results = MySQL.select("SELECT * FROM user WHERE email = ?", ps -> setString(ps, 1, email))
        ) {

            return mapToUser(results);
        }
    }

    /**
     * 사용자 정보를 저장합니다. 존재하지 않는 경우 새로 생성하고, 존재하는 경우 업데이트합니다.
     *
     * @param user 사용자 정보
     *
     * @return 저장된 사용자 정보
     */
    @SneakyThrows(SQLException.class)
    public User save(User user) {

        Optional<User> userOptional = findById(user.id());

        if (userOptional.isPresent()) {
            MySQL.update(
                    "UPDATE user SET email = ?, password = ?, name = ? WHERE id = ?", ps -> {
                        setString(ps, 1, user.email());
                        setString(ps, 2, user.password());
                        setString(ps, 3, user.name());
                        setInt(ps, 4, user.id());
                    }
            );

            return user;
        } else {
            MySQL.update(
                    "INSERT INTO user (email, password, name) VALUES (?, ?, ?)", ps -> {
                        setString(ps, 1, user.email());
                        setString(ps, 2, user.password());
                        setString(ps, 3, user.name());
                    }
            );

            return findByEmail(user.email()).orElseThrow();
        }
    }

    /**
     * ResultSet에서 사용자 정보를 매핑합니다.
     *
     * @param results ResultSet 객체
     *
     * @return 매핑된 사용자 정보
     */
    @SneakyThrows(SQLException.class)
    private Optional<User> mapToUser(ResultSet results) {

        if (!results.next()) {
            return Optional.empty();
        }

        return Optional.of(User.builder()
                .id(results.getInt("id"))
                .email(results.getString("email"))
                .password(results.getString("password"))
                .name(results.getString("name"))
                .build());
    }
}
