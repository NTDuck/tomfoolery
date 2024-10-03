package org.tomfoolery.configurations.monolith.gui.view.Components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class NavBar {
    private HBox navbar;

    public NavBar(double height, double spacing, double padding, ImageView menuLogo, HBox searchBar, Button profileIcon) {
        navbar = new HBox(spacing);
        navbar.setPadding(new Insets(padding));
        navbar.setPrefHeight(height);
        Region spacerLeft = new Region();
        spacerLeft.setMaxWidth(100);
        Region spacerRight = new Region();
        HBox.setHgrow(spacerRight, Priority.ALWAYS);
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        navbar.getChildren().addAll(menuLogo, spacerLeft, searchBar, spacerRight, profileIcon);
    }

    public HBox getNavBar(){
        return navbar;
    }
}