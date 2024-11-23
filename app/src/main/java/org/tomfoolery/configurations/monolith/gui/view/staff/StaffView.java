package org.tomfoolery.configurations.monolith.gui.view.staff;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.BaseView;
import org.tomfoolery.configurations.monolith.gui.view.user.Dashboard;
import org.tomfoolery.configurations.monolith.gui.view.user.Discover;
import java.io.IOException;

public class StaffView extends BaseView {
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
        StaffSidebar controller = new StaffSidebar();

        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/fxml/BaseSidebar.fxml"));
        sidebarLoader.setController(controller);
        sidebar = sidebarLoader.load();

        FXMLLoader documentsManagementButtonLoader = new FXMLLoader(getClass().getResource("/fxml/Staff/SidebarDocumentsManagementButton.fxml"));
        documentsManagementButtonLoader.setController(controller);
        Button documentsManagementButton = documentsManagementButtonLoader.load();

        documentsManagementButton.setOnAction(event -> controller.goToDocumentsManagement());

        VBox sidebarTopSection = (VBox) sidebar.getChildren().getFirst();
        sidebarTopSection.getChildren().add(documentsManagementButton);
    }

    private void loadContent(StageManager.ContentType contentType) throws IOException {
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

    private void loadDocumentsManagement() throws IOException {
        DocumentsManagementView controller = new DocumentsManagementView(
                StageManager.getInstance().getDocumentRepository(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository()
        );

        FXMLLoader documentsManagementLoader = new FXMLLoader(getClass().getResource("/fxml/Staff/DocumentsManagement.fxml"));
        documentsManagementLoader.setController(controller);
        content = documentsManagementLoader.load();
    }
}
