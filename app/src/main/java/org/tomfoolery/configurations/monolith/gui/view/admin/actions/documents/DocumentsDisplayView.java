package org.tomfoolery.configurations.monolith.gui.view.admin.actions.documents;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.documents.ShowDocumentsView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.common.documents.retrieval.ShowDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.retrieval.ShowDocumentsController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

public class DocumentsDisplayView extends ShowDocumentsView {
    public DocumentsDisplayView(
            @NonNull DocumentRepository documentRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository,
            @NonNull FileStorageProvider fileStorageProvider
    ) {
        super(documentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    @FXML
    private Label counterLabel;

    @FXML @Override
    public void initialize() {
        counterLabel.setText(this.getNumberOfDocuments() + " document(s)");
        showDocuments();
    }

    private int getNumberOfDocuments() {
        val requestModel = ShowDocumentsController.RequestObject.of(1, Integer.MAX_VALUE);
        try {
            val viewModel = this.controller.apply(requestModel);
            return viewModel.getPaginatedDocuments().size();
        } catch (ShowDocumentsUseCase.AuthenticationTokenInvalidException |
                 ShowDocumentsUseCase.AuthenticationTokenNotFoundException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (ShowDocumentsUseCase.PaginationInvalidException _) {
        }
        return 0;
    }
}
