package org.tomfoolery.configurations.monolith.gui.view.user;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class BaseSidebar {
    @FXML
    protected Button dashboardButton;

    @FXML
    protected Button discoverButton;

    @FXML
    protected Button profileButton;

    @FXML
    public void initialize() {
        dashboardButton.setOnAction(event -> goToDashboard());
        discoverButton.setOnAction(event -> goToDiscover());
    }

    protected abstract void goToDiscover();

    protected abstract void goToDashboard();
}
