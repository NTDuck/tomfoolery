package org.tomfoolery.configurations.monolith.gui.view.staff.layout;

import javafx.fxml.FXMLLoader;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.abc.BaseView;
import org.tomfoolery.configurations.monolith.gui.view.staff.actions.documents.DocumentsManagementView;
import org.tomfoolery.configurations.monolith.gui.view.staff.actions.documents.UpdateDocumentContentView;
import org.tomfoolery.configurations.monolith.gui.view.user.scenes.DashboardView;
import org.tomfoolery.configurations.monolith.gui.view.user.scenes.DiscoverView;
import java.io.IOException;

public class StaffView extends BaseView {
    @Override @SneakyThrows
    public void loadSidebar() {
        StaffSidebar controller = new StaffSidebar();

        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/fxml/Staff/StaffSidebar.fxml"));
        sidebarLoader.setController(controller);
        sidebar = sidebarLoader.load();
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
            case STAFF_SHOW_DOCUMENTS_WITHOUT_CONTENT:
                loadShowDocumentsWithoutContentView();
                break;
        }
    }

    @SneakyThrows
    private void loadDashboard() {
        DashboardView controller = new DashboardView(
                StageManager.getInstance().getResources().getDocumentRepository(),
                StageManager.getInstance().getResources().getBorrowingSessionRepository(),
                StageManager.getInstance().getResources().getDocumentRecommendationGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
                StageManager.getInstance().getResources().getFileStorageProvider()
        );

        FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/fxml/User/Dashboard.fxml"));
        dashboardLoader.setController(controller);
        content = dashboardLoader.load();
    }

    @SneakyThrows
    private void loadDiscover() {
        DiscoverView controller = new DiscoverView(
                StageManager.getInstance().getResources().getHybridDocumentRepository(),
                StageManager.getInstance().getResources().getDocumentSearchGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
                StageManager.getInstance().getResources().getFileStorageProvider()
        );

        FXMLLoader discoverLoader = new FXMLLoader(getClass().getResource("/fxml/User/Discover.fxml"));
        discoverLoader.setController(controller);
        content = discoverLoader.load();
    }

    @SneakyThrows
    private void loadDocumentsManagement() {
        DocumentsManagementView controller = new DocumentsManagementView(
                StageManager.getInstance().getResources().getDocumentRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
                StageManager.getInstance().getResources().getFileStorageProvider()
        );

        FXMLLoader documentsManagementLoader = new FXMLLoader(getClass().getResource("/fxml/Staff/DocumentsManagement.fxml"));
        documentsManagementLoader.setController(controller);
        content = documentsManagementLoader.load();
    }

    @SneakyThrows
    private void loadShowDocumentsWithoutContentView() {
        UpdateDocumentContentView controller = new UpdateDocumentContentView(
                StageManager.getInstance().getResources().getDocumentRepository(),
                StageManager.getInstance().getResources().getDocumentContentRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
                StageManager.getInstance().getResources().getFileStorageProvider(),
                StageManager.getInstance().getResources().getFileVerifier()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Staff/UpdateDocumentContentView.fxml"));
        loader.setController(controller);
        content = loader.load();
    }
}
