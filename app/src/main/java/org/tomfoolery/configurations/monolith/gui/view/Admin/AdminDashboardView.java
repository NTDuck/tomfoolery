package org.tomfoolery.configurations.monolith.gui.view.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.View;

public class AdminDashboardView extends View {
    public AdminDashboardView() {
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
        sidebarDiscoverButton.setOnAction(event -> goToDiscover());
        sidebarControlCenterButton.setOnAction(event -> goToControlCenter());
    }

    private void goToDiscover() {
        StageManager.getInstance().openMenu("/fxml/Admin/Discover.fxml");
    }

    private void goToControlCenter() {
        StageManager.getInstance().openMenu("/fxml/Admin/ControlCenter.fxml");
    }
}
