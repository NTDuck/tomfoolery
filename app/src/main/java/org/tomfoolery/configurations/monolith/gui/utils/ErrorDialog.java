package org.tomfoolery.configurations.monolith.gui.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.tomfoolery.configurations.monolith.gui.StageManager;

public class ErrorDialog {
    public static void open(String message) {
        // Outer VBox
        VBox outerVBox = new VBox();
        outerVBox.setAlignment(Pos.CENTER);
        outerVBox.setPrefSize(1920, 1080);

        // Inner VBox
        VBox innerVBox = new VBox();
        innerVBox.setAlignment(Pos.TOP_CENTER);
        innerVBox.setPrefSize(400, 250);
        innerVBox.setMaxSize(400, 250);
        innerVBox.setSpacing(20.0);
        innerVBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        innerVBox.setEffect(new DropShadow());
        innerVBox.setPadding(new Insets(10.0));

        // Label for "ERROR"
        Label errorLabel = new Label("ERROR");
        errorLabel.setTextFill(javafx.scene.paint.Color.RED);
        errorLabel.setFont(Font.font("Segoe UI Variable Bold", 50.0));

        // Label for message
        Label messageLabel = new Label(message);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setPrefWidth(200.0);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        messageLabel.setWrapText(true);
        messageLabel.setFont(Font.font(20.0));

        // VBox for Labels
        VBox labelVBox = new VBox();
        labelVBox.setAlignment(Pos.CENTER);
        labelVBox.getChildren().addAll(errorLabel, messageLabel);

        // Close Button
        Button closeButton = new Button("Close");
        closeButton.setPrefSize(80.0, 40.0);
        closeButton.setFont(Font.font(16.0));
        closeButton.setStyle("-fx-background-color: #a5bfe1; -fx-background-radius: 15;");
        closeButton.setOnMouseEntered(event -> closeButton.setStyle("-fx-background-color: #7a9bc7; -fx-cursor: hand;"));
        closeButton.setOnMouseExited(event -> closeButton.setStyle("-fx-background-color: #a5bfe1; -fx-cursor: default;"));
        closeButton.setOnAction(event -> {
            StageManager.getInstance().getRootStackPane().getChildren().removeLast();
        });

        // Add children to inner VBox
        innerVBox.getChildren().addAll(labelVBox, closeButton);

        // Add inner VBox to outer VBox
        outerVBox.getChildren().add(innerVBox);

        StageManager.getInstance().getRootStackPane().getChildren().add(outerVBox);
    }
}
