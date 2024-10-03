package org.tomfoolery.configurations.monolith.gui.view.Components;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class SearchBar {
    private HBox searchBar;
    private ImageView icon;

    public SearchBar(String pathToIcon, double width, double height) {
        icon = new ImageView(new Image(pathToIcon));
        icon.setFitHeight(height * 3 / 4);
        icon.setFitWidth(height * 3 / 4);

        TextField searchField = new TextField();
        searchField.setPromptText("Search a book...");
        searchField.setStyle("-fx-background-color: #E5E9F0; -fx-font-size: 24;");
        searchField.setPrefWidth(width - 30);
        searchField.setPrefHeight(height - 20);

        searchBar = new HBox(10);
        searchBar.setPrefWidth(width);
        searchBar.setPrefHeight(height);
        searchBar.setPadding(new Insets(10));
        searchBar.getChildren().addAll(icon, searchField);
        searchBar.setStyle("-fx-background-color: #E5E9F0; -fx-background-radius: 50;");
    }

    public HBox getSearchBar() {
        return searchBar;
    }
    
}
