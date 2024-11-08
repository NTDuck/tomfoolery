package org.tomfoolery.configurations.monolith.gui.view.Patron;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;

public class DashboardView {
    private final @NonNull StageManager stageManager;

    public DashboardView(@NonNull StageManager stageManager) {
        this.stageManager = stageManager;
    }

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

    }

    @FXML
    private void goToDiscover() {
        stageManager.openMenu("/fxml/Patron/Discover.fxml", "Discover");
    }
}