package org.tomfoolery.configurations.monolith.gui.view.Patron;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;

public class DiscoverView {
    private final @NonNull StageManager stageManager;

    public DiscoverView(@NonNull StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @FXML
    private Button sidebarDashboardButton;

    @FXML
    private Button sidebarDiscoverButton;

    @FXML
    private Button notificationButton;

    @FXML
    public void initialize() {
        sidebarDashboardButton.setOnAction(event -> {goToDashboard();});
    }

    @FXML
    private void goToDashboard() {
        stageManager.openMenu("/fxml/Patron/Dashboard.fxml", "Dashboard");
    }
}