package com.github.son_daehyeon.computer_network_socket.client.util;

import com.github.son_daehyeon.common.http.HttpRequest;
import com.github.son_daehyeon.common.http.HttpResponse;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Requester {

    @SneakyThrows(IOException.class)
    public static HttpResponse request(HttpRequest request) {

        String[] parts = ((String) request.getHeaders().getOrDefault("Host", "127.0.0.1:8080")).split(":");

        try (

                Socket socket = new Socket(parts[0], Integer.parseInt(parts[1]));
                BufferedWriter writer = new BufferedWriter(new PrintWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {

            writer.write(request.toHttpString());
            writer.flush();

            return HttpResponse.fromReader(reader);
        }
    }
}
