package org.tomfoolery.configurations.monolith.gui.view.Patron;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;

public class DashboardView {
    private final @NonNull StageManager stageManager;

    public DashboardView(@NonNull StageManager stageManager) {
        this.stageManager = stageManager;
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
        stageManager.openMenu("/fxml/Patron/Discover.fxml", "Discover");
    }
}