package org.tomfoolery.configurations.monolith.gui.view.staff;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.staff.documents.RemoveDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.RemoveDocumentController;

public class DeleteDocumentPopup {
    private final @NonNull RemoveDocumentController controller;
    private final @NonNull String documentISBN;

    public DeleteDocumentPopup(
            @NonNull String documentISBN,
            @NonNull DocumentRepository documentRepository,
            @NonNull PatronRepository patronRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository
    ) {
        this.documentISBN = documentISBN;
        this.controller = RemoveDocumentController.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @FXML
    private Button noButton;

    @FXML
    private Button yesButton;

    @FXML
    public void initialize() {
        noButton.setOnAction(event -> close());
        yesButton.setOnAction(event -> deleteDocument());
    }

    private void deleteDocument() {
        val requestObject = RemoveDocumentController.RequestObject.of(documentISBN);

        try {
            this.controller.accept(requestObject);
            close();
            StageManager.getInstance().loadStaffView(StageManager.ContentType.STAFF_DOCUMENTS_MANAGEMENT);
        } catch (RemoveDocumentUseCase.AuthenticationTokenNotFoundException e) {
            System.err.println("You are not a staff");
        } catch (RemoveDocumentUseCase.AuthenticationTokenInvalidException e) {
            System.err.println("Your staff token is invalid");
        } catch (RemoveDocumentUseCase.DocumentNotFoundException e) {
            System.err.println("This never happens btw");
        }
    }

    private void close() {
        StageManager.getInstance().getRootStackPane().getChildren().removeLast();
    }
}
