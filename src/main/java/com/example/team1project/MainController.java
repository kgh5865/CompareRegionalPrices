package com.example.team1project;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    String[] regions = {
            "강원도", "춘천시", "원주시", "강릉시", "동해시", "태백시", "속초시", "삼척시", "홍천군", "횡성군", "영월군", "평창군", "정선군", "철원군", "화천군", "양구군", "인제군", "고성군", "양양군"
    };
    String[] itemNames;

    @FXML
    private TextField input_search;
    @FXML
    private TextField input_price;
    @FXML
    private ListView<String> listview_search;
    @FXML
    private AnchorPane anchorpane_main;
    @FXML
    private ScrollPane scrollpane_selectedItem;
    @FXML
    private GridPane gridpane_regions;
    @FXML
    private Stage primaryStage;

    DataSingleton dataSingleton = DataSingleton.getInstance();
    private String selectedRegion = "";
    private ArrayList<CheckBox> regionCheckboxes = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        input_search.setFocusTraversable(false);
        listview_search.setVisible(false);

        setGridPane();// 지역 체크박스 초기 세팅

        /**** API 품목 가져오기 ****/

        // API 엔드포인트 URL
        String apiUrl = "https://chungcheong-price-api.vercel.app/products";

        // HTTP 클라이언트 생성
        HttpClient httpClient = HttpClient.newHttpClient();

        // HTTP 요청 생성
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        try {
            // HTTP 요청 보내고 응답 받기
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // 응답이 성공적으로 받아졌을 경우
            if (response.statusCode() == 200) {
                // Jackson ObjectMapper 생성
                ObjectMapper objectMapper = new ObjectMapper();

                // JSON 파싱
                JsonNode rootNode = objectMapper.readTree(response.body());

                // "data" 객체에서 "productNames" 배열 추출
                JsonNode productNamesArray = rootNode.path("data").path("productNames");
                ArrayList<String> itemNamesList = new ArrayList<>();

                // "productNames" 배열의 값을 출력
                for (JsonNode itemNameNode : productNamesArray) {
                    String itemName = itemNameNode.asText();
                    itemNamesList.add(itemName);
                }

                itemNames = itemNamesList.toArray(new String[0]);// 받은 데이터 입력
            } else {
                System.out.println("HTTP 요청이 실패했습니다. 상태 코드: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**** API 품목 가져오기 ****/

        // ListView 초기화
        ObservableList<String> itemList = FXCollections.observableArrayList(itemNames);
        listview_search.setItems(itemList);

        // 검색 기능 구현
        input_search.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<String> filteredList = FXCollections.observableArrayList();
            for (String item : itemNames) {
                if (item.toLowerCase().contains(newValue.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            listview_search.setItems(filteredList);

            // 동적으로 ListView의 높이 조절
            int numVisibleItems = Math.min(filteredList.size(), 5); // 최대 표시 아이템 수를 설정
            double itemHeight = 30.0; // 각 항목의 높이를 설정 (조절이 필요하면 적절한 값으로 변경)
            double newHeight = numVisibleItems * itemHeight;
            listview_search.setPrefHeight(newHeight);
        });

        // 리스트뷰 아이템 클릭 이벤트 처리 (선택된 아이템을 텍스트필드에 설정하고 리스트뷰 숨김)
        listview_search.setOnMouseClicked(event -> {
            String selectedItem = listview_search.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {// 선택한 아이템을 리스트에 추가
                input_search.setText(selectedItem);
                listview_search.setVisible(false);
                dataSingleton.setItem(selectedItem);// 싱글톤 값 넣기
            }
        });


        // 텍스트필드 외부 영역 클릭 시 리스트뷰 숨김
        input_search.setOnMousePressed(event -> {
            event.consume(); // 텍스트필드에 대한 클릭 이벤트 소비
            listview_search.setVisible(true); // 리스트뷰 표시
        });


        // Scene의 마우스 클릭 이벤트 처리
        anchorpane_main.setOnMousePressed(event -> {
            if (input_search.isFocused()) {
                input_search.setFocusTraversable(false);
            }
            if (listview_search.isVisible()) {
                listview_search.setVisible(false);
            }
        });

        // 이벤트 필터를 추가하여 숫자만 입력하도록 제어
        input_price.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            String newText = input_price.getText() + event.getCharacter();
            if (!character.matches("[0-9]") || newText.length() > 10) {// 숫자가 아니면 입력을 무시, 최대 길이 10
                event.consume();
            } else if (input_price.getText().equals("0")) {
                if (character.equals("0")) {// 0 연속 입력 방지
                    event.consume();
                } else if (character.matches("[1-9]")) {// 0 제거
                    input_price.setText("");
                }
            }
        });
    }

    private void setGridPane() {// 지역 체크박스 초기 세팅
        int colCount = 4; // 열의 개수
        int rowIndex = 0;
        int colIndex = 0;

        for (String item : regions) {
            CheckBox checkBox = new CheckBox(item);
            checkBox.setStyle("-fx-font-family: 'NanumGothic'; -fx-font-size: 16; -fx-background-color: #ffffff;");

            // CheckBox를 리스트에 추가
            regionCheckboxes.add(checkBox);

            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    selectedRegion = item;

                    // Uncheck other CheckBoxes
                    for (CheckBox cb : regionCheckboxes) {
                        if (cb != checkBox) {
                            cb.setSelected(false);
                        }
                    }
                }
            });

            gridpane_regions.add(checkBox, colIndex, rowIndex);

            // 열과 행 인덱스 업데이트
            colIndex++;
            if (colIndex == colCount) {
                colIndex = 0;
                rowIndex++;
            }
        }

        // ColumnConstraints 및 RowConstraints 설정
        for (int i = 0; i < colCount; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / colCount);
            gridpane_regions.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i < regions.length / colCount; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / (regions.length / colCount));
            gridpane_regions.getRowConstraints().add(rowConst);
        }
    }


    @FXML
    private void onResultDialog() {
        if (dataSingleton.getItem() == null) {
            System.out.println("물품을 선택하세요");
            return;
        }
        if (input_price.getText() == null || Objects.equals(input_price.getText(), "")) {
            System.out.println("가격을 입력하세요.");
            return;
        }

        dataSingleton.setRegion(selectedRegion);
        dataSingleton.setPrice(input_price.getText());

        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(primaryStage);

        Parent parent = null;
        try {
            parent = FXMLLoader.load(getClass().getResource("result.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(parent);
        dialog.setScene(scene);
        dialog.show();
    }
}