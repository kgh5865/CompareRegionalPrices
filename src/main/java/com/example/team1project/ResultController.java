package com.example.team1project;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ResultController implements Initializable {

    DataSingleton dataSingleton = DataSingleton.getInstance();
    @FXML
    private AnchorPane anchorpane;
    @FXML
    private Label resultText1, resultText2, resultText3;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 데이터 싱글톤에서 item을 얻어오고
        String targetItem = dataSingleton.getItem();
        String targetRegion = dataSingleton.getRegion();
        String targetPrice = dataSingleton.getPrice();


        // getPrices 메소드를 통해 지역별 가격을 map 객체에 불러오고
        Map<String, Integer> result = getPrices(targetItem);

        // result 정보를 이용하여 라벨 텍스트 수정하기
        setSuccessLabels(result);

        /****************그래프****************/
        // X축 및 Y축 생성

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("지역");
        yAxis.setLabel("가격");

        // y-축의 자동 범위 계산 비활성화
        yAxis.setAutoRanging(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("강원도 지역별 물가 그래프");

        // 데이터 추가 (예제에서는 간단하게 1부터 10까지의 데이터 추가)
        // String region = dataSingleton.getRegion();
        /*
        for (String region : regions) {
            series.getData().add(new XYChart.Data<>(region, result.get(region)));
        }
        */

        int max = 0, min = 0;// y축 최대, 최소
        int price;

        if (result == null) {
            System.out.println("map is null");
        } else {
            price = result.get("강원도");
            series.getData().add(new XYChart.Data<>("강원도", price));

            min = price;
            if (max < price) max = price;

            for (String r : result.keySet()) {
                if (r.equals("강원도")) continue;

                price = result.get(r);
                series.getData().add(new XYChart.Data<>(r, price));

                if (min == 0 || min > price) min = price;// 처음 받아올 때만 값을 받음
                if (max < price) max = price;
            }
        }

        max += 500;
        min -= 500;
        if (min < 500) min = 0;// 아무리 최소라도 0보다 낮지 않음

        yAxis.setLowerBound(min);
        yAxis.setUpperBound(max);
        yAxis.setTickUnit((max - min) / 12);

        // 라인 차트 생성 및 데이터 추가
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(targetItem + " | 강원도 물가 그래프");

        // lineChart Style
        lineChart.lookup(".chart-title").setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        lineChart.getXAxis().setTickLabelFont(javafx.scene.text.Font.font(null, javafx.scene.text.FontWeight.BOLD, 14));
        lineChart.getYAxis().setTickLabelFont(javafx.scene.text.Font.font(null, javafx.scene.text.FontWeight.BOLD, 13));


        // 차트에 데이터 추가
        lineChart.getData().add(series);

        // AnchorPane에 차트 추가
        anchorpane.getChildren().add(lineChart);

        // AnchorPane 내에서 차트 위치 설정
        anchorpane.setTopAnchor(lineChart, 10.0);
        anchorpane.setRightAnchor(lineChart, 60.0);
        anchorpane.setBottomAnchor(lineChart, 150.0);
        anchorpane.setLeftAnchor(lineChart, 60.0);
        /****************그래프****************/

    }

    // Label 텍스트를 결과에 맞게 바꿔주는 메소드입니다.
    private void setSuccessLabels(Map<String, Integer> result) {
        String item = dataSingleton.getItem();
        String region = dataSingleton.getRegion();
        int price = Integer.parseInt(dataSingleton.getPrice());

        // text1 설정
        StringBuilder text1StringBuilder = new StringBuilder();
        text1StringBuilder.append("입력 가격(").append(price).append("원)이 ").append(region).append("의 가격");

        int regionPrice = result.get(region);
        if (price > regionPrice) {
            text1StringBuilder.append("보다 높습니다.").append(" [+").append(price - regionPrice).append("원]");
        } else if (price < regionPrice) {
            text1StringBuilder.append("보다 낮습니다.").append(" [").append(price - regionPrice).append("원]");
        } else {
            text1StringBuilder.append("와 같습니다.");
        }


        resultText1.setText(text1StringBuilder.toString());


        // text2 설정
        resultText2.setText(region + "의 " + item + " 가격 : " + regionPrice + "원");
        // text3 설정
        resultText3.setText("강원도의 " + item + " 가격 : " + result.get("강원도") + "원");

        // result 들의 style
        resultText1.setStyle("-fx-font-size: 20px; -fx-font-weight: 600");
        resultText2.setStyle("-fx-font-size: 20px; -fx-font-weight: 600");
        resultText3.setStyle("-fx-font-size: 20px; -fx-font-weight: 600");

        // Label 들의 위치들을 조정합니다.
        anchorpane.setBottomAnchor(resultText1, 110.0);
        anchorpane.setBottomAnchor(resultText2, 75.0);
        anchorpane.setBottomAnchor(resultText3, 40.0);

        anchorpane.setRightAnchor(resultText1, 0.0);
        anchorpane.setLeftAnchor(resultText1, 0.0);
        anchorpane.setRightAnchor(resultText2, 0.0);
        anchorpane.setLeftAnchor(resultText2, 0.0);
        anchorpane.setRightAnchor(resultText3, 0.0);
        anchorpane.setLeftAnchor(resultText3, 0.0);

    }

    // 선택된 아이템의 지역별 가격을 Map 객체에 담아 return 하는 메소드
    private Map<String, Integer> getPrices(String item) {
        Map<String, Integer> pricesMap = new HashMap<>();

        if (item == null) {
            System.out.println("item is null");
            return pricesMap;
        }

        try {
            // 아이템을 UTF-8로 인코딩
            String encodedItem = URLEncoder.encode(item, StandardCharsets.UTF_8);

            // 인코딩된 아이템을 포함한 API 엔드포인트 URL
            String apiUrl = "https://chungcheong-price-api.vercel.app/prices?product=" + encodedItem;

            // HTTP 클라이언트 생성
            HttpClient httpClient = HttpClient.newHttpClient();

            // HTTP 요청 생성
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();

            // HTTP 요청 보내고 응답 받기
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // 응답이 성공적으로 받아졌을 경우
            if (response.statusCode() == 200) {
                // Jackson ObjectMapper 생성
                ObjectMapper objectMapper = new ObjectMapper();

                // JSON 파싱
                JsonNode rootNode = objectMapper.readTree(response.body());

                // "data" 객체에서 "prices" 배열 추출
                JsonNode pricesNode = rootNode.path("data").path("prices");

                // 각 지역의 가격을 맵에 추가
                pricesNode.fields().forEachRemaining(entry -> {
                    String location = entry.getKey();
                    int price = entry.getValue().asInt();
                    pricesMap.put(location, price);
                });
            } else {
                System.out.println("HTTP 요청이 실패했습니다. 상태 코드: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return pricesMap;
    }
}