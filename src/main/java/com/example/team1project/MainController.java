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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    String[] itemNames = {
            "설렁탕", "냉면", "삼계탕", "갈비탕", "불고기(공기밥제외)", "등심구이", "삼겹살(외식)", "된장찌개백반", "김치찌개백반", "튀김닭", "생선초밥",
            "비빔밥", "김밥", "칼국수", "라면(외식)", "자장면", "짬뽕", "탕수육", "돼지갈비", "돈가스", "쇠갈비", "햄버거", "피자", "다방커피", "다방 국산차",
            "PC방이용료", "택배수수료", "양복세탁료", "영화관람료", "수영장이용료", "볼링장이용료", "골프연습장이용료", "당구장이용료", "영상매체대여료", "노래방이용료", "사진촬영료",
            "사진인화료", "이용료", "미용료(드라이)", "미용료(파마)", "미용료(커트)", "목욕료(성인)", "목욕료(아동)", "숙박료(여관)", "학원비(중학생)", "생맥주",
            "경기장입장료", "찜질방이용료", "의복수선료"
    };

    String[] regions = {
            "공주시", "금산군", "논산시", "당진시", "보령시", "부여군", "서산시",
            "서천군", "아산시", "예산군", "천안시", "청양군", "태안군", "홍성군", "계룡시"
    };

    @FXML private TextField input_search;
    @FXML private TextField input_price;
    @FXML private ListView<String> listview_search;
    @FXML private AnchorPane anchorpane_main;
    @FXML private HBox scrollHBox_itemlist;
    @FXML private ScrollPane scrollpane_selectedItem;
    @FXML private GridPane gridpane_regions;
    @FXML private CheckBox checkbox_total;
    @FXML private Stage primaryStage;

    DataSingleton dataSingleton = DataSingleton.getInstance();

    private ArrayList<String> selectedItems = new ArrayList<>();
    private ArrayList<String> selectedRegions = new ArrayList<>();
    private ArrayList<CheckBox> regionCheckboxes = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        input_search.setFocusTraversable(false);
        listview_search.setVisible(false);

        setGridPane();//지역 체크박스 초기 세팅

        // ListView 초기화
        ObservableList<String> itemList = FXCollections.observableArrayList(itemNames);
        listview_search.setItems(itemList);

        // 검색 기능 구현
        input_search.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<String> filteredList = FXCollections.observableArrayList();
            for (String item : itemNames) {
                if (!selectedItems.contains(item) && item.toLowerCase().contains(newValue.toLowerCase())) {
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
                selectedItems.add(selectedItem);
                listview_search.getItems().remove(selectedItem);//리스트뷰에서 제거
                addNewItemButton(selectedItem);//버튼 추가
                input_search.setText("");
                listview_search.setVisible(false);
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
            if(listview_search.isVisible()){
                listview_search.setVisible(false);
            }
        });

        // 이벤트 필터를 추가하여 숫자만 입력하도록 제어
        input_price.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            String newText = input_price.getText() + event.getCharacter();
            if (!character.matches("[0-9]") || newText.length() > 10) {// 숫자가 아니면 입력을 무시, 최대 길이 10
                event.consume();
            }
            else if(input_price.getText().equals("0")){
                if(character.equals("0")){//0 연속 입력 방지
                    event.consume();
                }
                else if(character.matches("[1-9]")){//0 제거
                    input_price.setText("");
                }
            }
        });
    }

    private void addNewItemButton(String buttonText) {//버튼 추가
        Button button = new Button(buttonText+" X");

        // 버튼에 스타일 지정
        button.setStyle("-fx-font-family: 'NanumGothic'; -fx-font-size: 16; -fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 1; -fx-border-radius: 15;");

        // 버튼에 클릭 이벤트 핸들러 추가
        button.setOnAction(event -> {
            scrollHBox_itemlist.getChildren().remove(button);
            selectedItems.remove(buttonText);
            resetListItem();
        });

        // HBox에 버튼 추가
        scrollHBox_itemlist.getChildren().add(button);
    }

    private void resetListItem(){//리스트뷰 아이템 초기화
        ObservableList<String> filteredList = FXCollections.observableArrayList();
        for (String item : itemNames) {
            if (!selectedItems.contains(item)) {
                filteredList.add(item);
            }
        }
        listview_search.setItems(filteredList);
    }

    private void setGridPane(){//지역 체크박스 초기 세팅
        int colCount = 4; // 열의 개수
        int rowIndex = 0;
        int colIndex = 1;

        for (String item : regions) {
            CheckBox checkBox = new CheckBox(item);
            checkBox.setStyle("-fx-font-family: 'NanumGothic'; -fx-font-size: 16; -fx-background-color: #ffffff;");

            // CheckBox를 리스트에 추가
            regionCheckboxes.add(checkBox);

            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {// 체크박스 상태가 변경되었을 때의 동작
                if (newValue && !checkbox_total.isSelected()) {
                    selectedRegions.add(item);
                } else if(!newValue){
                    selectedRegions.remove(item);
                    if(checkbox_total.isSelected()){
                        checkbox_total.setSelected(false);
                    }
                }

                //System.out.println("Selected Regions: " + selectedRegions);
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
    private void handleTotalCheckboxAction() {
        // 전체 선택/해제 체크박스의 상태에 따라 모든 체크박스 선택/해제
        boolean isSelected = checkbox_total.isSelected();

        // 모든 지역을 선택 상태에 따라 추가 또는 제거
        selectedRegions.clear();
        if (isSelected) {
            selectedRegions.addAll(Arrays.asList(regions));
        }

        for (CheckBox checkBox : regionCheckboxes) {
            checkBox.setSelected(isSelected);
        }
    }


    @FXML
    private void onResultDialog() {
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