package org.tomfoolery.configurations.monolith.gui.view.Components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MenuLogo {
    private ImageView logo;

    public void setSize(double height, double width) {
        logo.setFitHeight(height);
        logo.setFitWidth(width);
    }

    public MenuLogo(String pathToLogo, double height, double width) {
        logo = new ImageView(new Image(pathToLogo));
        logo.setFitHeight(height);
        logo.setFitWidth(width);
    }

    public ImageView getLogo() {
        return logo;
    }
}