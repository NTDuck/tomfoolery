package org.tomfoolery.configurations.monolith.gui.view.patron.layout;

import javafx.fxml.FXMLLoader;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.abc.BaseView;
import org.tomfoolery.configurations.monolith.gui.view.patron.actions.auth.PatronAccountCenter;
import org.tomfoolery.configurations.monolith.gui.view.patron.actions.documents.ShowBorrowedDocumentsView;
import org.tomfoolery.configurations.monolith.gui.view.user.scenes.DashboardView;
import org.tomfoolery.configurations.monolith.gui.view.user.scenes.DiscoverView;

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

    @SneakyThrows
    private void loadDashboard() {
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

    @SneakyThrows
    private void loadDiscover() {
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

    @SneakyThrows
    private void loadShowBorrowedDocuments() {
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

    @SneakyThrows
    private void loadAccountCenter() {
        PatronAccountCenter controller = new PatronAccountCenter(
                StageManager.getInstance().getResources().getPatronRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
                StageManager.getInstance().getResources().getPasswordEncoder(),
                StageManager.getInstance().getResources().getDocumentRepository(),
                StageManager.getInstance().getResources().getBorrowingSessionRepository()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Patron/PatronAccountCenter.fxml"));
        loader.setController(controller);
        content = loader.load();
    }
}
