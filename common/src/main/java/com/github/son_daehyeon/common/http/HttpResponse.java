package com.github.son_daehyeon.common.http;

import com.github.son_daehyeon.common.constant.HttpStatus;
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
public class HttpResponse {

    private final HttpVersion version;

    private final HttpStatus status;

    @Singular
    private final Map<String, Object> headers;

    @Singular
    private final Map<String, Object> bodies;

    /**
     * HttpResponse 객체를 HTTP 응답 문자열 InputStream으로부터 생성합니다.
     *
     * @return HttpResponse 객체
     */
    @SneakyThrows(IOException.class)
    public static HttpResponse fromReader(BufferedReader reader) {

        int contentLength = 0;
        HttpResponseBuilder builder = HttpResponse.builder();

        // Response Line
        {
            String[] parts = waitForResponse(reader).split(" ", 2);

            builder.version(HttpVersion.fromVersion(parts[0].split("/")[1]))
                    .status(HttpStatus.fromCode(Integer.parseInt(parts[1].split(" ")[0])));
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

                if (reader.read(chars) == contentLength) {

                    builder.bodies(JsonMapper.fromJson(new String(chars)));
                }
            }
        }

        return builder.build();
    }

    /**
     * BufferedReader로부터 HTTP 응답을 기다립니다.
     *
     * @param reader BufferedReader
     *
     * @return HTTP 응답 문자열
     */
    private static String waitForResponse(BufferedReader reader) throws IOException {

        String line;

        //noinspection StatementWithEmptyBody
        while ((line = reader.readLine()) == null)
            ;

        return line;
    }

    /**
     * HTTP 응답에 Content-Type과 Content-Length 헤더를 추가합니다.
     *
     * @return HttpResponse 객체
     */
    public HttpResponse addContentHeader() {

        return this.toBuilder()
                .header("Content-Type", "application/json")
                .header("Content-Length", bodies.isEmpty() ? 0 : JsonMapper.toJson(bodies).length())
                .build();
    }

    /**
     * HTTP 응답 문자열로 변환합니다.
     *
     * @return HTTP 응답 문자열
     */
    public String toHttpString() {

        StringBuilder http = new StringBuilder();

        // Response Line
        http.append(version.toString()).append(" ").append(status.toString()).append("\r\n");

        // Header
        headers.forEach((key, value) -> http.append(key).append(": ").append(value).append("\r\n"));

        http.append("\r\n");

        // Body
        http.append(JsonMapper.toJson(bodies));

        return http.toString();
    }
}