package org.tomfoolery.configurations.monolith.gui.view.patron;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.BaseSidebar;

public class PatronSidebar extends BaseSidebar {
    @FXML
    private Button showBorrowedDocumentsButton;

    @Override
    public void goToDashboard() {
        StageManager.getInstance().loadPatronView(StageManager.ContentType.PATRON_DASHBOARD);
    }

    @Override
    public void goToDiscover() {
        StageManager.getInstance().loadPatronView(StageManager.ContentType.PATRON_DISCOVER);
    }

    public void goToShowBorrowedDocumentsView() {
        StageManager.getInstance().loadPatronView(StageManager.ContentType.PATRON_SHOW_BORROWED);
    }
}
