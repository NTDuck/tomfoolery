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

        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/fxml/BaseSidebar.fxml"));
        sidebarLoader.setController(controller);
        sidebar = sidebarLoader.load();

        FXMLLoader showBorrowedButtonLoader = new FXMLLoader(getClass().getResource("/fxml/Patron/ShowBorrowedDocumentsButton.fxml"));
        showBorrowedButtonLoader.setController(controller);
        Button showBorrowedDocuments = showBorrowedButtonLoader.load();

        showBorrowedDocuments.setOnAction(event -> controller.goToShowBorrowedDocumentsView());

        VBox sidebarTopSection = (VBox) sidebar.getChildren().getFirst();
        sidebarTopSection.getChildren().add(showBorrowedDocuments);

        FXMLLoader accountCenterButtonLoader = new FXMLLoader(getClass().getResource("/fxml/Patron/AccountCenterButton.fxml"));
        accountCenterButtonLoader.setController(controller);
        Button accountCenterButton = accountCenterButtonLoader.load();

        accountCenterButton.setOnAction(event -> controller.goToAccountCenter());

        VBox sidebarBottomSection = (VBox) sidebar.getChildren().getLast();
        sidebarBottomSection.getChildren().addFirst(accountCenterButton);
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
        DashboardView controller = new DashboardView();

        FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
        dashboardLoader.setController(controller);
        content = dashboardLoader.load();
    }

    private void loadDiscover() throws IOException {
        DiscoverView controller = new DiscoverView(
                StageManager.getInstance().getDocumentSearchGenerator(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository()
        );

        FXMLLoader discoverLoader = new FXMLLoader(getClass().getResource("/fxml/Discover.fxml"));
        discoverLoader.setController(controller);
        content = discoverLoader.load();
    }

    private void loadShowBorrowedDocuments() throws IOException {
        ShowBorrowedDocumentsView controller = new ShowBorrowedDocumentsView(
                StageManager.getInstance().getDocumentRepository(),
                StageManager.getInstance().getDocumentContentRepository(),
                StageManager.getInstance().getBorrowingSessionRepository(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Patron/ShowBorrowedDocumentsView.fxml"));
        loader.setController(controller);
        content = loader.load();
    }

    private void loadAccountCenter() {

    }
}
