package org.tomfoolery.configurations.monolith.gui.view.Components;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProfileIcon {
    private Button profileIcon;

    public ProfileIcon(String pathToIcon, double size) {
        profileIcon = new Button();
        ImageView icon = new ImageView(new Image(pathToIcon));
        icon.setFitWidth(size);
        icon.setFitHeight(size);
        profileIcon.setGraphic(icon);
        profileIcon.setPrefWidth(size);
        profileIcon.setPrefHeight(size);
        profileIcon.setStyle("-fx-background-radius: 50; -fx-background-color: #E5E9F0");
    }

    public Button getIcon() {
        return profileIcon;
    }
}
