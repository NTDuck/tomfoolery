package org.tomfoolery.configurations.monolith.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
        Scene scene = new Scene(root, Color.LIGHTBLUE);

        stage.setMinHeight(720);
        stage.setMinWidth(1280);
        stage.setWidth(1600);
        stage.setHeight(900);
        Image icon = new Image(getClass().getResourceAsStream("/logo.png"));
        stage.getIcons().add(icon);
        stage.setTitle("Tomfoolery - Library Management App");
        stage.setScene(scene);
        stage.show();
    }
}