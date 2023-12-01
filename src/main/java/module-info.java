module com.example.team1project {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.team1project to javafx.fxml;
    exports com.example.team1project;
}