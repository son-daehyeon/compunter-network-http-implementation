package com.github.son_daehyeon.common.http;

import com.github.son_daehyeon.common.constant.HttpMethod;
import com.github.son_daehyeon.common.constant.HttpVersion;
import com.github.son_daehyeon.common.util.JsonMapper;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class HttpRequest {

    private final HttpMethod method;

    private final HttpVersion version;

    private final String path;

    @Singular
    private final Map<String, Object> headers;

    @Singular
    private final Map<String, Object> bodies;

    /**
     * HttpRequest 객체를 HTTP 요청 문자열 InputStream으로부터 생성합니다.
     *
     * @return HttpRequest 객체
     */
    @SneakyThrows(IOException.class)
    public static HttpRequest fromReader(BufferedReader reader) {

        int contentLength = 0;
        HttpRequestBuilder builder = HttpRequest.builder();

        // Request Line
        {
            String[] parts = waitForRequest(reader).split(" ", 3);

            builder.method(HttpMethod.valueOf(parts[0]))
                    .path(parts[1])
                    .version(HttpVersion.fromVersion(parts[2].split("/")[1]));
        }

        // Header
        {
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {

                String[] parts = line.split(": ", 2);

                if (parts[0].equalsIgnoreCase("Content-Length")) {

                    contentLength = Integer.parseInt(parts[1]);
                }

                builder.header(parts[0], parts[1]);
            }
        }

        // Body
        {
            if (contentLength > 0) {
                char[] chars = new char[contentLength];

                if (reader.read(chars) != contentLength) {

                    throw new RuntimeException("헤더의 Content-Length와 본문 길이가 일치하지 않습니다.");
                }

                builder.bodies(JsonMapper.fromJson(new String(chars)));
            }
        }

        return builder.build();
    }

    /**
     * BufferedReader로부터 HTTP 요청을 기다립니다.
     *
     * @param reader BufferedReader
     *
     * @return HTTP 요청 문자열
     */
    private static String waitForRequest(BufferedReader reader) throws IOException {

        String line;

        //noinspection StatementWithEmptyBody
        while ((line = reader.readLine()) == null)
            ;

        return line;
    }

    /**
     * HTTP 요청에 Host 헤더를 추가합니다.
     *
     * @param host 서버 호스트
     *
     * @return HttpRequest 객체
     */
    public HttpRequest addHostHeader(String host, int port) {

        return this.toBuilder().header("Host", host + ':' + port).build();
    }

    /**
     * HTTP 응답에 Content-Type과 Content-Length 헤더를 추가합니다.
     *
     * @return HttpResponse 객체
     */
    public HttpRequest addContentHeader() {

        return this.toBuilder()
                .header("Content-Type", "application/json")
                .header("Content-Length", bodies.isEmpty() ? 0 : JsonMapper.toJson(bodies).length())
                .build();
    }

    /**
     * HTTP 요청 문자열로 변환합니다.
     *
     * @return HTTP 요청 문자열
     */
    public String toHttpString() {

        StringBuilder http = new StringBuilder();

        // Request Line
        http.append(method.toString()).append(" ").append(path).append(" ").append(version.toString()).append("\r\n");

        // Header
        headers.forEach((key, value) -> http.append(key).append(": ").append(value).append("\r\n"));

        http.append("\r\n");

        // Body
        http.append(JsonMapper.toJson(bodies));

        return http.toString();
    }
}