package org.tomfoolery.configurations.monolith.gui.view.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.tomfoolery.configurations.monolith.gui.StageManager;

import java.io.IOException;

public class ControlCenter {
    private final DocumentsDisplayView documentsDisplayView;
    private final AccountsDisplayView accountsDisplayView;

    public ControlCenter() {
        documentsDisplayView = new DocumentsDisplayView(
                StageManager.getInstance().getDocumentRepository(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository()
        );
        accountsDisplayView = new AccountsDisplayView();
    }

    @FXML
    private Button viewDocumentsButton;

    @FXML
    private Button viewAccountsButton;

    @FXML
    private VBox tableDisplay;

    @FXML
    public void initialize() {
        viewDocumentsButton.setOnAction(event -> viewDocuments());
        viewAccountsButton.setOnAction(event -> viewAccounts());
        viewDocuments();
    }

    private void viewDocuments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/DocumentsDisplay.fxml"));
            loader.setController(documentsDisplayView);
            VBox v = loader.load();

            tableDisplay.getChildren().clear();
            tableDisplay.getChildren().setAll(v);
        } catch (IOException e) {
            System.err.println("Error loading documents display view fxml");
        }
    }

    private void viewAccounts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/AccountsDisplay.fxml"));
            loader.setController(accountsDisplayView);
            VBox v = loader.load();

            tableDisplay.getChildren().clear();
            tableDisplay.getChildren().setAll(v);
        } catch (IOException e) {
            System.err.println("Error loading documents display view fxml");
        }
    }
}
