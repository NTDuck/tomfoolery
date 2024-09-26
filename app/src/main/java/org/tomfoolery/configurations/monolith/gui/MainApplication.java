package org.tomfoolery.configurations.monolith.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MainApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Pane root = new Pane();
        Scene scene = new Scene(root, Color.valueOf("#4c566a"));

        Image appIcon = new Image(getClass().getResourceAsStream("/logo.png"));
        Image background = new Image(getClass().getResourceAsStream("/background.png"));
        ImageView backgroundView = new ImageView(background);
        backgroundView.fitWidthProperty().bind(scene.widthProperty());
        backgroundView.fitHeightProperty().bind(scene.heightProperty());

        Rectangle panel1 = new Rectangle(400, 400, Color.web("#ffffff"));
        panel1.setY(scene.getHeight() / 2 + 300);
        panel1.setX(0);

        root.getChildren().addAll(backgroundView, panel1);

        stage.getIcons().add(appIcon);
        stage.setHeight(900);
        stage.setWidth(1600);
        // stage.setMaximized(true);
        stage.setTitle("Tomfoolery");
        stage.setScene(scene);
        stage.show();
    }
}