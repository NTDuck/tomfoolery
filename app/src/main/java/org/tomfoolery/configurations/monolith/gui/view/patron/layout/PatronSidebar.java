package org.tomfoolery.configurations.monolith.gui.view.patron.layout;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.abc.BaseSidebar;

public class PatronSidebar extends BaseSidebar {
    @FXML
    private Button showBorrowedDocumentsButton;

    @FXML
    private Button accountCenterButton;

    @Override @FXML
    public void initialize() {
        super.initialize();
        showBorrowedDocumentsButton.setOnAction(event -> goToShowBorrowedDocumentsView());
        accountCenterButton.setOnAction(event -> goToAccountCenter());
    }

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

    public void goToAccountCenter() {
        StageManager.getInstance().loadPatronView(StageManager.ContentType.PATRON_ACCOUNT_CENTER);
    }
}
