package org.tomfoolery.configurations.monolith.gui.view.staff.layout;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.abc.BaseSidebar;

public class StaffSidebar extends BaseSidebar {
    @FXML
    private Button documentsManagementButton;

    @Override @FXML
    public void initialize() {
        super.initialize();
        documentsManagementButton.setOnAction(event -> goToDocumentsManagement());
    }

    @Override
    public void goToDashboard() {
        StageManager.getInstance().loadStaffView(StageManager.ContentType.STAFF_DASHBOARD);
    }

    @Override
    public void goToDiscover() {
        StageManager.getInstance().loadStaffView(StageManager.ContentType.STAFF_DISCOVER);
    }

    public void goToDocumentsManagement() {
        StageManager.getInstance().loadStaffView(StageManager.ContentType.STAFF_DOCUMENTS_MANAGEMENT);
    }
}