package com.example.team1project;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.Initializable;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ResultController implements Initializable {

    DataSingleton dataSingleton = DataSingleton.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 데이터 싱글톤에서 item을 얻어오고
        String targetItem = dataSingleton.getItem();

        // getPrices 메소드를 통해 지역별 가격을 map 객체에 불러오고
        Map<String, Integer> result = getPrices(targetItem);

        // map에 잘 들어왔는지 테스트
        if (result == null) {
            System.out.println("map is null");
        } else {
            for (String r : result.keySet()) {
                System.out.println(r + " : " + result.get(r));
            }
        }
    }

    // 선택된 아이템의 지역별 가격을 Map 객체에 담아 return 하는 메소드
    private Map<String, Integer> getPrices(String item) {
        Map<String, Integer> pricesMap = new HashMap<>();

        if (item == null) {
            System.out.println("item is null");
            return pricesMap;
        }


        try {

            String encodedItem = URLEncoder.encode(item, StandardCharsets.UTF_8);

            // API endpoint URL with encoded item parameter
            String apiUrl = "https://chungcheong-price-api.vercel.app/prices?product=" + encodedItem;

            // HTTP client creation
            HttpClient httpClient = HttpClient.newHttpClient();

            // HTTP request creation
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();

            // Send HTTP request and receive response
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // If the response is successful
            if (response.statusCode() == 200) {
                // Jackson ObjectMapper creation
                ObjectMapper objectMapper = new ObjectMapper();

                // JSON parsing
                JsonNode rootNode = objectMapper.readTree(response.body());

                // Extract prices from "data" object
                JsonNode pricesNode = rootNode.path("data").path("prices");

                // Iterate over the prices and add them to the map
                pricesNode.fields().forEachRemaining(entry -> {
                    String location = entry.getKey();
                    int price = entry.getValue().asInt();
                    pricesMap.put(location, price);
                });
            } else {
                System.out.println("HTTP request failed. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return pricesMap;
    }
}