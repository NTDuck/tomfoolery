package org.tomfoolery.configurations.monolith.gui.view.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.BaseSidebar;

public class AdminSidebar extends BaseSidebar {
    @FXML
    private Button controlCenterButton;

    @Override
    public void goToDashboard() {
        StageManager.getInstance().loadAdminView("Dashboard");
    }

    @Override
    public void goToDiscover() {
        StageManager.getInstance().loadAdminView("Discover");
    }

    public void goToControlCenter() {
        StageManager.getInstance().loadAdminView("ControlCenter");
    }
}
