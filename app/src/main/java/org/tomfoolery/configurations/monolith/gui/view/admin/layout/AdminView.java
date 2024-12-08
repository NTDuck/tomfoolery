package org.tomfoolery.configurations.monolith.gui.view.admin.layout;

import javafx.fxml.FXMLLoader;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.abc.BaseView;
import org.tomfoolery.configurations.monolith.gui.view.user.scenes.DashboardView;
import org.tomfoolery.configurations.monolith.gui.view.user.scenes.DiscoverView;
import java.io.IOException;

public class AdminView extends BaseView {
    @SneakyThrows @Override
    public void loadSidebar() {
        AdminSidebar controller = new AdminSidebar();

        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/fxml/Admin/AdminSidebar.fxml"));
        sidebarLoader.setController(controller);
        sidebar = sidebarLoader.load();
    }

    @SneakyThrows @Override
    public void loadContent(StageManager.@NonNull ContentType contentType) {
        switch (contentType) {
            case ADMIN_DASHBOARD:
                loadDashboard();
                break;
            case ADMIN_DISCOVER:
                loadDiscover();
                break;
            case ADMIN_CONTROL_CENTER:
                loadControlCenter();
                break;
        }
    }

    private void loadDashboard() throws IOException {
        DashboardView controller = new DashboardView(
                StageManager.getInstance().getResources().getDocumentRepository(),
                StageManager.getInstance().getResources().getBorrowingSessionRepository(),
                StageManager.getInstance().getResources().getDocumentRecommendationGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository()
        );

        FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/fxml/User/Dashboard.fxml"));
        dashboardLoader.setController(controller);
        content = dashboardLoader.load();
    }

    private void loadDiscover() throws IOException {
        DiscoverView controller = new DiscoverView(
                StageManager.getInstance().getResources().getHybridDocumentRepository(),
                StageManager.getInstance().getResources().getDocumentSearchGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository()
        );

        FXMLLoader discoverLoader = new FXMLLoader(getClass().getResource("/fxml/User/Discover.fxml"));
        discoverLoader.setController(controller);
        content = discoverLoader.load();
    }

    private void loadControlCenter() throws IOException {
        ControlCenter controller = new ControlCenter();

        FXMLLoader controlCenterLoader = new FXMLLoader(getClass().getResource("/fxml/Admin/ControlCenter.fxml"));
        controlCenterLoader.setController(controller);
        content = controlCenterLoader.load();
    }
}
