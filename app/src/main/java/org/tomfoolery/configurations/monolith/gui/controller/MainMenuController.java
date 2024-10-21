package org.tomfoolery.configurations.monolith.gui.controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class MainMenuController {
    @FXML
    private HBox navigationBar;

    @FXML
    private VBox sidebar;

    @FXML
    private HBox content;

    @FXML
    public void initialize() throws IOException {
        loadNavigationBar();
        loadSidebar();
        loadContent("/fxml/Discover.fxml");
    }

    @FXML
    private void loadSidebar() {
        try {
            VBox side = FXMLLoader.load(getClass().getResource("/fxml/Sidebar.fxml"));
            sidebar.getChildren().setAll(side);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadNavigationBar() {
        try {
            HBox nav = FXMLLoader.load(getClass().getResource("/fxml/NavigationBar.fxml"));
            navigationBar.getChildren().setAll(nav);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadContent(String contentViewFXML) {
        try {
            HBox cont = FXMLLoader.load(getClass().getResource(contentViewFXML));
            content.getChildren().setAll(cont);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}