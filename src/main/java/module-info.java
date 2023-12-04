module com.example.team1project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;


    opens com.example.team1project to javafx.fxml;
    exports com.example.team1project;
}