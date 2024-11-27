package org.tomfoolery.configurations.monolith.gui.view.patron;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.BaseView;
import org.tomfoolery.configurations.monolith.gui.view.user.Dashboard;
import org.tomfoolery.configurations.monolith.gui.view.user.Discover;

import java.io.IOException;

public class PatronView extends BaseView {
    @Override @SneakyThrows
    public void loadSidebar() {
        PatronSidebar controller = new PatronSidebar();

        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/fxml/BaseSidebar.fxml"));
        sidebarLoader.setController(controller);
        sidebar = sidebarLoader.load();

        FXMLLoader buttonLoader = new FXMLLoader(getClass().getResource("/fxml/Patron/ShowBorrowedDocumentsButton.fxml"));
        buttonLoader.setController(controller);
        Button showBorrowedDocuments = buttonLoader.load();

        showBorrowedDocuments.setOnAction(event -> controller.goToShowBorrowedDocumentsView());

        VBox sidebarTopSection = (VBox) sidebar.getChildren().getFirst();
        sidebarTopSection.getChildren().add(showBorrowedDocuments);
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

    private void loadShowBorrowedDocuments() throws IOException {
        ShowBorrowedDocumentsView controller = new ShowBorrowedDocumentsView(
                StageManager.getInstance().getDocumentRepository(),
                StageManager.getInstance().getPatronRepository(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Patron/ShowBorrowedDocumentsView.fxml"));
        loader.setController(controller);
        content = loader.load();
    }
}
