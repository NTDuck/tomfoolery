package org.tomfoolery.configurations.monolith.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;

public class StageManager {
    private static @Getter Stage primaryStage;

    private StageManager() {
    }

    public static void setPrimaryStage(Stage stage) {
        StageManager.primaryStage = stage;
    }

    public static void openMainMenu() {
        try {
            Parent root = FXMLLoader.load(StageManager.class.getResource("/fxml/MainMenu.fxml"));

            primaryStage.setMinHeight(720);
            primaryStage.setMinWidth(1280);
            primaryStage.setWidth(1600);
            primaryStage.setHeight(900);
            Image icon = new Image(StageManager.class.getResourceAsStream("/images/logo.png"));
            primaryStage.getIcons().add(icon);
            primaryStage.setTitle("Tomfoolery - Library Management App");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}