package com.example.team1project;

import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;

public class JavaTextFont extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        Font.loadFont(getClass().getResourceAsStream("/resources/NanumGothic.ttf"), 10);
        Parent root = FXMLLoader.load(getClass().getResource("mainview.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
