package org.tomfoolery.configurations.monolith.gui.view.admin;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.BaseView;
import org.tomfoolery.configurations.monolith.gui.view.user.Dashboard;
import org.tomfoolery.configurations.monolith.gui.view.user.Discover;

import java.io.IOException;

public class AdminView extends BaseView {
    public void loadView(String contentType) {
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
        AdminSidebar controller = new AdminSidebar();

        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/fxml/BaseSidebar.fxml"));
        sidebarLoader.setController(controller);
        sidebar = sidebarLoader.load();

        FXMLLoader controlCenterButtonLoader = new FXMLLoader(getClass().getResource("/fxml/Admin/SidebarControlCenterButton.fxml"));
        controlCenterButtonLoader.setController(controller);
        Button controlCenterButton = controlCenterButtonLoader.load();

        controlCenterButton.setOnAction(event -> controller.goToControlCenter());

        VBox sidebarTopSection = (VBox) sidebar.getChildren().getFirst();
        sidebarTopSection.getChildren().add(controlCenterButton);
    }

    private void loadContent(String contentType) throws IOException {
        switch (contentType) {
            case "Dashboard":
                loadDashboard();
                break;
            case "Discover":
                loadDiscover();
                break;
            case "ControlCenter":
                loadControlCenter();
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

    private void loadControlCenter() throws IOException {
        ControlCenter controller = new ControlCenter();

        FXMLLoader controlCenterLoader = new FXMLLoader(getClass().getResource("/fxml/Admin/ControlCenter.fxml"));
        controlCenterLoader.setController(controller);
        content = controlCenterLoader.load();
    }
}
