package org.tomfoolery.configurations.monolith.gui.view.user.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.common.documents.recommendation.abc.GetDocumentRecommendationUseCase;
import org.tomfoolery.core.usecases.patron.documents.borrow.retrieval.ShowBorrowedDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.recommendation.GetDocumentRecommendationController;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.borrow.retrieval.ShowBorrowedDocumentsController;

public class DashboardView {
    private final @NonNull GetDocumentRecommendationController getRecommendationController;
    private final @NonNull ShowBorrowedDocumentsController showBorrowedDocumentsController;

    public DashboardView(@NonNull DocumentRepository documentRepository,
                         @NonNull BorrowingSessionRepository borrowingSessionRepository,
                         @NonNull DocumentRecommendationGenerator documentRecommendationGenerator,
                         @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                         @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.getRecommendationController = GetDocumentRecommendationController.of(
                documentRepository,
                documentRecommendationGenerator,
                authenticationTokenGenerator,
                authenticationTokenRepository
        );
        this.showBorrowedDocumentsController = ShowBorrowedDocumentsController.of(
                documentRepository,
                borrowingSessionRepository,
                authenticationTokenGenerator,
                authenticationTokenRepository
        );
    }

    @FXML
    private Label numberOfBorrowedDocuments;

    @FXML
    private Label numberOfPatrons;

    @FXML
    private ImageView recommendedDocumentCoverImage;

    @FXML
    private Label recommendedDocumentTitle;

    @FXML
    private Label recommendedDocumentAuthor;

    @FXML
    public void initialize() {
        numberOfBorrowedDocuments.setText(String.valueOf(getNumberOfBorrowedDocuments()));
        numberOfPatrons.setText(String.valueOf(StageManager.getInstance().getResources().getPatronRepository().show().size()));
        this.loadRecommendation();
    }

    private void loadRecommendation() {
        val requestObject = this.getRequestObject();
        try {
            val viewModel = this.getRecommendationController.apply(requestObject);
            this.onSuccess(viewModel);
        } catch (GetDocumentRecommendationUseCase.AuthenticationTokenNotFoundException |
                 GetDocumentRecommendationUseCase.AuthenticationTokenInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    private GetDocumentRecommendationController.@NonNull RequestObject getRequestObject() {
        return GetDocumentRecommendationController.RequestObject.of(GetDocumentRecommendationController.RecommendationType.TOP_RATED);
    }

    private void onSuccess(GetDocumentRecommendationController.@NonNull ViewModel viewModel) {
        val documentsList = viewModel.getDocumentRecommendation();
        val document = documentsList.getFirst();
        recommendedDocumentTitle.setText(document.getDocumentTitle());
        recommendedDocumentAuthor.setText(String.join(",", document.getDocumentAuthors()));
    }

    private @Unsigned int getNumberOfBorrowedDocuments() {
        try {
            val viewModel = this.showBorrowedDocumentsController.apply(ShowBorrowedDocumentsController.RequestObject.of(1, Integer.MAX_VALUE));
            return viewModel.getPaginatedBorrowedDocuments().size();
        } catch (ShowBorrowedDocumentsUseCase.AuthenticationTokenInvalidException |
                 ShowBorrowedDocumentsUseCase.AuthenticationTokenNotFoundException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (ShowBorrowedDocumentsUseCase.PaginationInvalidException e) {
            return 0;
        }
        return 0;
    }
}
