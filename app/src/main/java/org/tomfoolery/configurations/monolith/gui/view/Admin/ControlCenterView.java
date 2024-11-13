package org.tomfoolery.configurations.monolith.gui.view.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.View;

public class ControlCenterView extends View {
    public ControlCenterView() {
    }

    @FXML
    private Button sidebarDashboardButton;

    @FXML
    private Button sidebarDiscoverButton;

    @FXML
    private Button sidebarControlCenterButton;

    @FXML
    private Button sidebarNotificationButton;

    @FXML
    private Button sidebarProfileButton;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        sidebarDashboardButton.setOnAction(event -> goToDashboard());
        sidebarDiscoverButton.setOnAction(event -> goToDiscover());
    }

    private void goToDashboard() {
        StageManager.getInstance().openMenu("/fxml/Admin/Dashboard.fxml");
    }

    private void goToDiscover() {
        StageManager.getInstance().openMenu("/fxml/Admin/Discover.fxml");
    }

}
