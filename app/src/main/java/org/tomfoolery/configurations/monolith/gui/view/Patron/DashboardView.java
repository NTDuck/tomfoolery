package org.tomfoolery.configurations.monolith.gui.view.Patron;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import java.io.IOException;

public class MainMenuView {
    @FXML
    private Pane content = new Pane();

    @FXML
    private Button sidebarDashboardButton;

    @FXML
    private Button sidebarDiscoverButton;

    @FXML
    private Button notificationButton;

    @FXML
    private MenuButton profileButton;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        goToDashboard();
    }

    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            content = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void goToDashboard() {
        loadContent("/fxml/Patron/Dashboard.fxml");
    }

    @FXML
    private void goToDiscover() {
        loadContent("/fxml/Patron/Discover.fxml");
    }
}