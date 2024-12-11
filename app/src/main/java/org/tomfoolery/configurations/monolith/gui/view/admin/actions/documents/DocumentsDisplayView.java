package org.tomfoolery.configurations.monolith.gui.view.admin.actions.documents;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.documents.ShowDocumentsView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.infrastructures.adapters.controllers.internal.statistics.GetStatisticsController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

public class DocumentsDisplayView extends ShowDocumentsView {
    private final @NonNull GetStatisticsController getStatisticsController = GetStatisticsController.of(
            StageManager.getInstance().getResources().getDocumentRepository(),
            StageManager.getInstance().getResources().getAdministratorRepository(),
            StageManager.getInstance().getResources().getPatronRepository(),
            StageManager.getInstance().getResources().getStaffRepository()
    );

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
        counterLabel.setText(this.getStatisticsController.get().getNumberOfDocuments() + " document(s)");
        showDocuments();
    }
}
