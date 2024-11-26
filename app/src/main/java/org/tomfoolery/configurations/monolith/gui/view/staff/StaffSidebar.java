package org.tomfoolery.configurations.monolith.gui.view.staff;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.BaseSidebar;

public class StaffSidebar extends BaseSidebar {
    @FXML
    private Button documentsManagement;

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