package org.tomfoolery.configurations.monolith.gui.view.patron.layout;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.abc.BaseView;
import org.tomfoolery.configurations.monolith.gui.view.patron.actions.documents.ShowBorrowedDocumentsView;
import org.tomfoolery.configurations.monolith.gui.view.user.scenes.DashboardView;
import org.tomfoolery.configurations.monolith.gui.view.user.scenes.DiscoverView;

import java.io.IOException;

public class PatronView extends BaseView {
    @Override @SneakyThrows
    public void loadSidebar() {
        PatronSidebar controller = new PatronSidebar();

        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/fxml/Patron/PatronSidebar.fxml"));
        sidebarLoader.setController(controller);
        sidebar = sidebarLoader.load();
    }

    @Override @SneakyThrows
    public void loadContent(StageManager.@NonNull ContentType contentType) {
        switch (contentType) {
            case PATRON_DASHBOARD:
                loadDashboard();
                break;
            case PATRON_DISCOVER:
                loadDiscover();
                break;
            case PATRON_SHOW_BORROWED:
                loadShowBorrowedDocuments();
                break;
            case PATRON_ACCOUNT_CENTER:
                loadAccountCenter();
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

    private void loadShowBorrowedDocuments() throws IOException {
        ShowBorrowedDocumentsView controller = new ShowBorrowedDocumentsView(
                StageManager.getInstance().getResources().getHybridDocumentRepository(),
                StageManager.getInstance().getResources().getDocumentContentRepository(),
                StageManager.getInstance().getResources().getBorrowingSessionRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Patron/ShowBorrowedDocumentsView.fxml"));
        loader.setController(controller);
        content = loader.load();
    }

    private void loadAccountCenter() {

    }
}
