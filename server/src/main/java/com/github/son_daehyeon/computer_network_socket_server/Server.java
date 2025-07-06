package com.github.son_daehyeon.computer_network_socket_server;

import com.github.son_daehyeon.common.http.HttpRequest;
import com.github.son_daehyeon.common.http.HttpResponse;
import com.github.son_daehyeon.computer_network_socket_server.router.Router;
import com.github.son_daehyeon.computer_network_socket_server.util.MySQL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 서버 코드 진입점
 */
public class Server {

    // 서버 포트
    private static final int PORT = 8080;

    // 라우터 초기화
    static {

        MySQL.initialize();

        System.out.println(
                "====================================================================================================");
        System.out.println("Registering routes.");

        Router.initialize();

        System.out.println(
                "====================================================================================================");
        System.out.println();
    }

    public static void main(String[] args) throws IOException {

        // 서버 소켓 생성
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Server is running on port " + PORT + ".\n");

            Socket socket;

            // 클라이언트 연결 대기
            while ((socket = serverSocket.accept()) != null) {

                try (

                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                ) {

                    HttpRequest request = HttpRequest.fromReader(reader);

                    System.out.println(
                            "====================================================================================================");

                    System.out.println("  - 메소드: " + request.getMethod());
                    System.out.println("  - 버전: " + request.getVersion());
                    System.out.println("  - 경로: " + request.getPath());
                    System.out.println("  - 헤더: ");
                    request.getHeaders().forEach((key, value) -> System.out.println("    - " + key + ": " + value));
                    System.out.println("  - 바디:");
                    request.getBodies().forEach((key, value) -> System.out.println("    - " + key + ": " + value));

                    System.out.println(
                            "====================================================================================================");

                    System.out.println();

                    HttpResponse response = Router.handle(request); // 요청 처리

                    writer.write(response.toHttpString());
                    writer.flush();
                }
            }
        }
    }
}