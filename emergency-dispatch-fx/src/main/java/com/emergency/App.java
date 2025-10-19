package com.emergency;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // This line finds and loads your FXML UI file
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        
        Scene scene = new Scene(root, 1200, 800);
        stage.setTitle("Emergency Dispatch System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}