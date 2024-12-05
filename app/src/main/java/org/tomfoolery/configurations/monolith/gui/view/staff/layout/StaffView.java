package org.tomfoolery.configurations.monolith.gui.view.staff.layout;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.abc.BaseView;
import org.tomfoolery.configurations.monolith.gui.view.staff.actions.documents.DocumentsManagementView;
import org.tomfoolery.configurations.monolith.gui.view.user.scenes.DashboardView;
import org.tomfoolery.configurations.monolith.gui.view.user.scenes.DiscoverView;
import java.io.IOException;

public class StaffView extends BaseView {
    @Override @SneakyThrows
    public void loadSidebar() {
        StaffSidebar controller = new StaffSidebar();

        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/fxml/User/BaseSidebar.fxml"));
        sidebarLoader.setController(controller);
        sidebar = sidebarLoader.load();

        FXMLLoader documentsManagementButtonLoader = new FXMLLoader(getClass().getResource("/fxml/Staff/SidebarDocumentsManagementButton.fxml"));
        documentsManagementButtonLoader.setController(controller);
        Button documentsManagementButton = documentsManagementButtonLoader.load();

        documentsManagementButton.setOnAction(event -> controller.goToDocumentsManagement());

        VBox sidebarTopSection = (VBox) sidebar.getChildren().getFirst();
        sidebarTopSection.getChildren().add(documentsManagementButton);
    }

    @Override @SneakyThrows
    public void loadContent(StageManager.@NonNull ContentType contentType) {
        switch (contentType) {
            case STAFF_DASHBOARD:
                loadDashboard();
                break;
            case STAFF_DISCOVER:
                loadDiscover();
                break;
            case STAFF_DOCUMENTS_MANAGEMENT:
                loadDocumentsManagement();
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
                StageManager.getInstance().getResources().getDocumentRepository(),
                StageManager.getInstance().getResources().getDocumentSearchGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository()
        );

        FXMLLoader discoverLoader = new FXMLLoader(getClass().getResource("/fxml/User/Discover.fxml"));
        discoverLoader.setController(controller);
        content = discoverLoader.load();
    }

    private void loadDocumentsManagement() throws IOException {
        DocumentsManagementView controller = new DocumentsManagementView(
                StageManager.getInstance().getResources().getDocumentRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository()
        );

        FXMLLoader documentsManagementLoader = new FXMLLoader(getClass().getResource("/fxml/Staff/DocumentsManagement.fxml"));
        documentsManagementLoader.setController(controller);
        content = documentsManagementLoader.load();
    }
}
