package com.github.son_daehyeon.common.constant;

/**
 * HTTP 버전 목록
 */
public enum HttpVersion {

    HTTP_1_0("1.0"),
    HTTP_1_1("1.1"),
    HTTP_2_0("2.0"),
    HTTP_3_0("3.0"),
    ;

    private final String version;

    HttpVersion(String version) {

        this.version = version;
    }

    /**
     * 주어진 버전 값에 해당하는 HttpVersion 열거형 값을 반환합니다.
     *
     * @param version HTTP 버전 값
     *
     * @return 해당하는 HttpVersion 열거형 값
     *
     * @throws IllegalArgumentException 알 수 없는 HTTP 버전인 경우
     */
    public static HttpVersion fromVersion(String version) {

        for (HttpVersion httpVersion : values()) {
            if (httpVersion.version.equals(version)) {
                return httpVersion;
            }
        }
        throw new IllegalArgumentException("Unknown HTTP version: " + version);
    }

    /**
     * HTTP 버전 문자열을 반환합니다.
     *
     * @return HTTP 버전 문자열
     */
    @Override
    public String toString() {

        return "HTTP/" + version;
    }
}