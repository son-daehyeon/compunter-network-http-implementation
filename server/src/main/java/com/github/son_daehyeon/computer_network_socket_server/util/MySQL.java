package com.github.son_daehyeon.computer_network_socket_server.util;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * MySQL 데이터베이스 유틸리티 클래스
 */
public class MySQL {

    private static final String MYSQL_HOST = "localhost";
    private static final int MYSQL_PORT = 3306;
    private static final String MYSQL_NAME = "example_user";
    private static final String MYSQL_USERNAME = "root";
    private static final String MYSQL_PASSWORD = "root";

    private static final Connection connection;

    /*
      MySQL 데이터베이스 연결을 초기화합니다.
     */
    static {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + MYSQL_HOST + ":" + MYSQL_PORT + "/" + MYSQL_NAME,
                    MYSQL_USERNAME,
                    MYSQL_PASSWORD
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initialize() {

        @SuppressWarnings("unused") Connection connection1 = connection;
    }

    public static ResultSet select(String sql) throws SQLException {

        return select(
                sql, statement -> {
                }
        );
    }

    public static ResultSet select(String sql, Consumer<PreparedStatement> consumer) throws SQLException {

        @SuppressWarnings("SqlSourceToSinkFlow") PreparedStatement statement = connection.prepareStatement(sql);

        consumer.accept(statement);

        return statement.executeQuery();
    }

    public static void update(String sql, Consumer<PreparedStatement> consumer) throws SQLException {

        @SuppressWarnings("SqlSourceToSinkFlow") PreparedStatement statement = connection.prepareStatement(sql);

        consumer.accept(statement);

        statement.executeUpdate();
    }

    @SneakyThrows(SQLException.class)
    public static void setInt(PreparedStatement statement, int index, int value) {

        statement.setInt(index, value);
    }

    @SneakyThrows(SQLException.class)
    public static void setString(PreparedStatement statement, int index, String value) {

        statement.setString(index, value);
    }
}
