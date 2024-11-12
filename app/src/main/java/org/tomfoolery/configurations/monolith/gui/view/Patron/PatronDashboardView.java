package org.tomfoolery.configurations.monolith.gui.view.Patron;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.View;

public class PatronDashboardView extends View {
    public PatronDashboardView() {
    }

    @FXML
    private Button sidebarDashboardButton;

    @FXML
    private Button sidebarDiscoverButton;

    @FXML
    private Button sidebarNotificationButton;

    @FXML
    private Button sidebarProfileButton;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        sidebarDiscoverButton.setOnAction(event -> {goToDiscover();});
    }

    private void goToDiscover() {
        StageManager.getInstance().openMenu("/fxml/Patron/Discover.fxml");
    }
}