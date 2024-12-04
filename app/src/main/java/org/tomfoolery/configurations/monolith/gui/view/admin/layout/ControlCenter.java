package org.tomfoolery.configurations.monolith.gui.view.admin.layout;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.admin.actions.documents.DocumentsDisplayView;
import org.tomfoolery.configurations.monolith.gui.view.admin.actions.staffs.StaffAccountsManagementView;

public class ControlCenter {
    private final DocumentsDisplayView documentsDisplayView;
    private final StaffAccountsManagementView accountsDisplayView;

    public ControlCenter() {
        documentsDisplayView = new DocumentsDisplayView(
                StageManager.getInstance().getDocumentRepository(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository()
        );
        accountsDisplayView = new StaffAccountsManagementView(
                StageManager.getInstance().getStaffRepository(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository()
        );
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

    @SneakyThrows
    private void viewDocuments() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/DocumentsDisplay.fxml"));
        loader.setController(documentsDisplayView);
        VBox v = loader.load();

        tableDisplay.getChildren().clear();
        tableDisplay.getChildren().setAll(v);
    }

    @SneakyThrows
    private void viewAccounts() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/StaffAccountsManagementView.fxml"));
        loader.setController(accountsDisplayView);
        VBox v = loader.load();

        tableDisplay.getChildren().clear();
        tableDisplay.getChildren().setAll(v);
    }
}
