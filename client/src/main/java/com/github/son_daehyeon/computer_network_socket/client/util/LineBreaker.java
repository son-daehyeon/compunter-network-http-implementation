package com.github.son_daehyeon.computer_network_socket.client.util;

import java.util.Scanner;

public class LineBreaker {

    private static final Scanner scanner = new Scanner(System.in);

    public static void breakLine() {

        System.out.println("다음 요청을 진행하려면 엔터를 누르세요.");
        scanner.nextLine();
    }
}
