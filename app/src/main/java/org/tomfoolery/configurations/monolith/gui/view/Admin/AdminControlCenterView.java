package org.tomfoolery.configurations.monolith.gui.view.Admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.View;

import java.io.IOException;

public class AdminControlCenterView extends View {
    private DocumentsDisplayView documentsDisplayView;
    private AccountsDisplayView accountsDisplayView;

    public AdminControlCenterView() {
    }

    @FXML
    private Button sidebarDashboardButton;

    @FXML
    private Button sidebarDiscoverButton;

    @FXML
    private Button sidebarControlCenterButton;

    @FXML
    private Button sidebarNotificationButton;

    @FXML
    private Button sidebarProfileButton;

    @FXML
    private Button viewDocumentsButton;

    @FXML
    private Button viewAccountsButton;

    @FXML
    private VBox tableDisplay;

    @FXML
    public void initialize() {
        sidebarDashboardButton.setOnAction(event -> goToDashboard());
        sidebarDiscoverButton.setOnAction(event -> goToDiscover());
        viewDocumentsButton.setOnAction(event -> viewDocuments());
        viewAccountsButton.setOnAction(event -> viewAccounts());
        viewDocuments();
    }

    private void goToDashboard() {
        StageManager.getInstance().openMenu("/fxml/Admin/Dashboard.fxml");
    }

    private void goToDiscover() {
        StageManager.getInstance().openMenu("/fxml/Admin/Discover.fxml");
    }

    private void viewDocuments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/DocumentsDisplay.fxml"));
            loader.setController(documentsDisplayView);
            tableDisplay.getChildren().clear();
            VBox v = loader.load();
            tableDisplay.getChildren().setAll(v);
        } catch (IOException e) {
            System.err.println("Error loading documents display view fxml");
        }
    }

    private void viewAccounts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/AccountsDisplay.fxml"));
            loader.setController(accountsDisplayView);
            tableDisplay.getChildren().clear();
            VBox v = loader.load();
            tableDisplay.getChildren().setAll(v);
        } catch (IOException e) {
            System.err.println("Error loading documents display view fxml");
        }
    }
}
