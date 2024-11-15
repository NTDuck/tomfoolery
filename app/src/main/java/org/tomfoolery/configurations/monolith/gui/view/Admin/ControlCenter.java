package org.tomfoolery.configurations.monolith.gui.view.Admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ControlCenter {
    private DocumentsDisplayView documentsDisplayView;
    private AccountsDisplayView accountsDisplayView;

    public ControlCenter() {
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
