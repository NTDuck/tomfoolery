package org.tomfoolery.configurations.monolith.gui.view.admin.layout;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.admin.actions.documents.DocumentsDisplayView;
import org.tomfoolery.configurations.monolith.gui.view.admin.actions.patrons.PatronAccountsManagementView;
import org.tomfoolery.configurations.monolith.gui.view.admin.actions.staffs.StaffAccountsManagementView;

public class ControlCenter {
    private final DocumentsDisplayView documentsDisplayView;
    private final StaffAccountsManagementView staffAccountsDisplayView;
    private final PatronAccountsManagementView patronAccountsDisplayView;

    public ControlCenter() {
        documentsDisplayView = new DocumentsDisplayView(
                StageManager.getInstance().getResources().getDocumentRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
                StageManager.getInstance().getResources().getFileStorageProvider()
        );
        staffAccountsDisplayView = new StaffAccountsManagementView(
                StageManager.getInstance().getResources().getStaffRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository()
        );
        patronAccountsDisplayView = new PatronAccountsManagementView(
                StageManager.getInstance().getResources().getPatronRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository()
        );
    }

    @FXML
    private Button viewDocumentsButton;

    @FXML
    private Button viewAccountsButton;

    @FXML
    private Button viewPatronsButton;

    @FXML
    private VBox tableDisplay;

    @FXML
    public void initialize() {
        viewDocumentsButton.setOnAction(event -> viewDocuments());
        viewAccountsButton.setOnAction(event -> viewAccounts());
        viewPatronsButton.setOnAction(event -> viewPatrons());
        viewDocuments();
    }

    @SneakyThrows
    private void viewDocuments() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/DocumentsDisplayView.fxml"));
        loader.setController(documentsDisplayView);
        VBox v = loader.load();

        tableDisplay.getChildren().clear();
        tableDisplay.getChildren().setAll(v);
    }

    @SneakyThrows
    private void viewAccounts() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/StaffAccountsManagementView.fxml"));
        loader.setController(staffAccountsDisplayView);
        VBox v = loader.load();

        tableDisplay.getChildren().clear();
        tableDisplay.getChildren().setAll(v);
    }

    @SneakyThrows
    private void viewPatrons() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/PatronAccountsManagementView.fxml"));
        loader.setController(patronAccountsDisplayView);
        VBox v = loader.load();

        tableDisplay.getChildren().clear();
        tableDisplay.getChildren().setAll(v);
    }
}
