package com.github.son_daehyeon.computer_network_socket.server.util;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.UnifiedJedis;

/**
 * Redis 데이터베이스 유틸리티 클래스
 */
public class Redis {

    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;

    private static final UnifiedJedis jedis = new UnifiedJedis(new HostAndPort(REDIS_HOST, REDIS_PORT));

    /**
     * Redis에 키-값 쌍을 저장합니다.
     *
     * @param key 저장할 키
     */
    public static void set(String key, String value, int seconds) {

        jedis.setex(key, seconds, value);
    }

    /**
     * Redis에서 키에 해당하는 값을 가져옵니다.
     *
     * @param key 가져올 키
     *
     * @return 키에 해당하는 값, 키가 존재하지 않으면 null
     */
    public static String get(String key) {

        return jedis.get(key);
    }

    /**
     * Redis에서 키에 해당하는 값을 삭제합니다.
     *
     * @param key 삭제할 키
     */
    public static void delete(String key) {

        jedis.del(key);
    }

    /**
     * Redis에 키가 존재하는지 확인합니다.
     *
     * @param key 확인할 키
     *
     * @return 키의 존재 여부
     */
    public static boolean exists(String key) {

        return jedis.exists(key);
    }
}
