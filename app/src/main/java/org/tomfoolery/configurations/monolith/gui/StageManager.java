package org.tomfoolery.configurations.monolith.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.Getter;
import org.tomfoolery.configurations.monolith.gui.view.LoginView;

import java.io.IOException;

public class StageManager {
    private static @Getter Stage primaryStage;

    private StageManager() {
    }

    public static void setPrimaryStage(Stage stage) {
        StageManager.primaryStage = stage;
    }

    public static void openLoginMenu() {
        try {
            Parent root = FXMLLoader.load(StageManager.class.getResource("/fxml/LoginMenu.fxml"));

            Scene scene = new Scene(root);
            primaryStage.setHeight(800);
            primaryStage.setWidth(600);
            primaryStage.setResizable(false);
            Image icon = new Image(StageManager.class.getResourceAsStream("/images/logo.png"));
            primaryStage.getIcons().add(icon);
            primaryStage.setTitle("Tomfoolery - Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void openMainMenu(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(StageManager.class.getResource(fxmlPath));

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

    public static void openSignupMenu() {
        try {
            Parent root = FXMLLoader.load(StageManager.class.getResource("/fxml/SignupMenu.fxml"));

            Scene scene = new Scene(root);
            primaryStage.setHeight(800);
            primaryStage.setWidth(600);
            primaryStage.setResizable(false);
            Image icon = new Image(StageManager.class.getResourceAsStream("/images/logo.png"));
            primaryStage.getIcons().add(icon);
            primaryStage.setTitle("Tomfoolery - Sign up");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}