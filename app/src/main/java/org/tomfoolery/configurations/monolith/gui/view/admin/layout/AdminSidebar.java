package org.tomfoolery.configurations.monolith.gui.view.admin.layout;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.abc.BaseSidebar;

public class AdminSidebar extends BaseSidebar {
    @FXML
    private Button controlCenterButton;

    public AdminSidebar() {
        super();
    }

    @Override
    public void initialize() {
        super.initialize();
        controlCenterButton.setOnAction(event -> goToControlCenter());
    }

    @Override
    public void goToDashboard() {
        StageManager.getInstance().loadAdminView(StageManager.ContentType.ADMIN_DASHBOARD);
    }

    @Override
    public void goToDiscover() {
        StageManager.getInstance().loadAdminView(StageManager.ContentType.ADMIN_DISCOVER);
    }

    public void goToControlCenter() {
        StageManager.getInstance().loadAdminView(StageManager.ContentType.ADMIN_CONTROL_CENTER);
    }
}
