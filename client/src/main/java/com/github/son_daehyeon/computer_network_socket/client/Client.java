package com.github.son_daehyeon.computer_network_socket.client;

import com.github.son_daehyeon.common.http.HttpResponse;
import com.github.son_daehyeon.computer_network_socket.client.scenario.Scenario;
import com.github.son_daehyeon.computer_network_socket.client.scenario.ScenarioList;
import com.github.son_daehyeon.computer_network_socket.client.util.LineBreaker;
import com.github.son_daehyeon.computer_network_socket.client.util.Requester;

import java.util.List;

/**
 * 클라이언트 코드 진입점
 */
public class Client {

    public static void main(String[] args) {

        List<Scenario> scenarios = ScenarioList.getScenarios();

        for (int i = 0; i < scenarios.size(); i++) {

            Scenario scenario = scenarios.get(i);

            HttpResponse response = Requester.request(scenario.request().get());
            scenario.then().accept(response);

            System.out.println(
                    "====================================================================================================");

            System.out.println("#" + String.format("%02d", i + 1) + ". " + scenario.title());

            System.out.println("  - 버전: " + response.getVersion());
            System.out.println("  - 응답: " + response.getStatus());
            System.out.println("  - 헤더: ");
            response.getHeaders().forEach((key, value) -> System.out.println("    - " + key + ": " + value));
            System.out.println("  - 바디:");
            response.getBodies().forEach((key, value) -> System.out.println("    - " + key + ": " + value));

            System.out.println(
                    "====================================================================================================");

            System.out.println();

            if (i < scenarios.size() - 1) {
                LineBreaker.breakLine();
            }
        }
    }
}