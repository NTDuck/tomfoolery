package org.tomfoolery.configurations.monolith.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StageManager.setPrimaryStage(primaryStage);

        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/fxml/LoginMenu.fxml"));
        Parent root = loginLoader.load();

        Scene scene = new Scene(root);
        primaryStage.setHeight(800);
        primaryStage.setWidth(600);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Tomfoolery - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}