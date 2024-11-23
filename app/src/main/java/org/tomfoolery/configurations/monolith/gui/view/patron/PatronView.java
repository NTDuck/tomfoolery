package org.tomfoolery.configurations.monolith.gui.view.patron;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.BaseView;
import org.tomfoolery.configurations.monolith.gui.view.user.Dashboard;
import org.tomfoolery.configurations.monolith.gui.view.user.Discover;

import java.io.IOException;

public class PatronView extends BaseView {
    public void loadView(StageManager.ContentType contentType) {
        try {
            loadSidebar();
            loadContent(contentType);

            mainView.getChildren().add(sidebar);
            mainView.getChildren().add(content);

            StageManager.getInstance().setMainStageProperties();
            StageManager.getInstance().getPrimaryStage().setScene(new Scene(mainView));

            StageManager.getInstance().getPrimaryStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadSidebar() throws IOException {
        PatronSidebar controller = new PatronSidebar();

        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/fxml/BaseSidebar.fxml"));
        sidebarLoader.setController(controller);
        sidebar = sidebarLoader.load();
    }

    private void loadContent(StageManager.ContentType contentType) throws IOException {
        switch (contentType) {
            case PATRON_DASHBOARD:
                loadDashboard();
                break;
            case PATRON_DISCOVER:
                loadDiscover();
                break;
        }
    }

    private void loadDashboard() throws IOException {
        Dashboard controller = new Dashboard();

        FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
        dashboardLoader.setController(controller);
        content = dashboardLoader.load();
    }

    private void loadDiscover() throws IOException {
        Discover controller = new Discover(
                StageManager.getInstance().getDocumentRepository(),
                StageManager.getInstance().getDocumentSearchGenerator(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository()
        );

        FXMLLoader discoverLoader = new FXMLLoader(getClass().getResource("/fxml/Discover.fxml"));
        discoverLoader.setController(controller);
        content = discoverLoader.load();
    }
}
