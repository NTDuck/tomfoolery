package org.tomfoolery.configurations.monolith.gui.view.patron;

import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.BaseSidebar;

public class PatronSidebar extends BaseSidebar {
    @Override
    public void goToDashboard() {
        StageManager.getInstance().loadPatronView(StageManager.ContentType.PATRON_DASHBOARD);
    }

    @Override
    public void goToDiscover() {
        StageManager.getInstance().loadPatronView(StageManager.ContentType.PATRON_DISCOVER);
    }
}
