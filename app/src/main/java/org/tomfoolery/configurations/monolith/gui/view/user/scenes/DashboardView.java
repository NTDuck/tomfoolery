package org.tomfoolery.configurations.monolith.gui.view.user.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.usecases.external.common.documents.recommendation.abc.GetDocumentRecommendationUseCase;
import org.tomfoolery.core.usecases.external.patron.documents.borrow.retrieval.ShowBorrowedDocumentsUseCase;
import org.tomfoolery.core.usecases.external.patron.users.retrieval.GetPatronUsernameAndMetadataUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.recommendation.GetDocumentRecommendationController;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.documents.borrow.retrieval.ShowBorrowedDocumentsController;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.users.retrieval.GetPatronUsernameAndMetadataController;
import org.tomfoolery.infrastructures.adapters.controllers.internal.statistics.GetStatisticsController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

public class DashboardView {
    private final @NonNull GetDocumentRecommendationController getRecommendationController;
    private final @NonNull ShowBorrowedDocumentsController showBorrowedDocumentsController;
    private final @NonNull GetStatisticsController getStatisticsController;

    public DashboardView(@NonNull DocumentRepository documentRepository,
                         @NonNull BorrowingSessionRepository borrowingSessionRepository,
                         @NonNull DocumentRecommendationGenerator documentRecommendationGenerator,
                         @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                         @NonNull AuthenticationTokenRepository authenticationTokenRepository,
                         @NonNull FileStorageProvider fileStorageProvider
                         ) {
        this.getRecommendationController = GetDocumentRecommendationController.of(
                documentRecommendationGenerator,
                authenticationTokenGenerator,
                authenticationTokenRepository,
                fileStorageProvider
        );
        this.showBorrowedDocumentsController = ShowBorrowedDocumentsController.of(
                documentRepository,
                borrowingSessionRepository,
                authenticationTokenGenerator,
                authenticationTokenRepository,
                fileStorageProvider
        );
        this.getStatisticsController = GetStatisticsController.of(
                StageManager.getInstance().getResources().getDocumentRepository(),
                StageManager.getInstance().getResources().getAdministratorRepository(),
                StageManager.getInstance().getResources().getPatronRepository(),
                StageManager.getInstance().getResources().getStaffRepository()
        );
    }

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label numberOfBorrowedDocuments;

    @FXML
    private Label numberOfPatrons;

    @FXML
    private Label numberOfAvailableDocuments;

    @FXML
    private ImageView topRatedCoverImage;

    @FXML
    private Label topRatedTitle;

    @FXML
    private Label topRatedAuthors;

    @FXML
    private ImageView recentCoverImage;

    @FXML
    private Label recentTitle;

    @FXML
    private Label recentAuthors;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome back" + getCurrentPatronName());
        numberOfBorrowedDocuments.setText(String.valueOf(getNumberOfBorrowedDocuments()));
        numberOfPatrons.setText(String.valueOf(getNumberOfPatrons()));
        numberOfAvailableDocuments.setText(String.valueOf(getNumberOfDocuments()));
        
        this.loadRecommendation();
    }

    private @NonNull String getCurrentPatronName() {
        String name = "";
        final @NonNull GetPatronUsernameAndMetadataController controller = GetPatronUsernameAndMetadataController.of(
                StageManager.getInstance().getResources().getPatronRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository()
        );
        try {
            val viewModel = controller.get();
            name = ", " + viewModel.getPatronFirstName() + " " + viewModel.getPatronLastName();
        } catch (GetPatronUsernameAndMetadataUseCase.AuthenticationTokenNotFoundException |
                 GetPatronUsernameAndMetadataUseCase.AuthenticationTokenInvalidException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (GetPatronUsernameAndMetadataUseCase.PatronNotFoundException _) {
        }
        return name;
    }

    private @Unsigned int getNumberOfPatrons() {
        val viewModel = this.getStatisticsController.get();
        return viewModel.getNumberOfPatrons();
    }

    private void loadRecommendation() {
        val topRatedRequestObject = this.getTopRatedRequestObject();
        val recentRequestObject = this.getRecentRequestObject();

        try {
            val topRatedViewModel = this.getRecommendationController.apply(topRatedRequestObject);
            this.topRatedOnSuccess(topRatedViewModel);

            val recentViewModel = this.getRecommendationController.apply(recentRequestObject);
            this.recentOnSuccess(recentViewModel);
        } catch (GetDocumentRecommendationUseCase.AuthenticationTokenNotFoundException |
                 GetDocumentRecommendationUseCase.AuthenticationTokenInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    private GetDocumentRecommendationController.@NonNull RequestObject getTopRatedRequestObject() {
        return GetDocumentRecommendationController.RequestObject.of(GetDocumentRecommendationController.RecommendationType.TOP_RATED);
    }

    private GetDocumentRecommendationController.@NonNull RequestObject getRecentRequestObject() {
        return GetDocumentRecommendationController.RequestObject.of(GetDocumentRecommendationController.RecommendationType.LATEST);
    }

    private void topRatedOnSuccess(GetDocumentRecommendationController.@NonNull ViewModel viewModel) {
        val documentsList = viewModel.getDocumentRecommendation();
        if (documentsList.isEmpty()) {
            topRatedCoverImage.setImage(null);
            topRatedTitle.setText("No top rated documents");
            topRatedAuthors.setText(null);
            return;
        }
        val document = documentsList.getFirst();
        if (document.getDocumentCoverImageFilePath().endsWith(".gif")) {
            topRatedCoverImage.setImage(new Image("/images/default/placeholder-book-cover.png"));
        } else {
            topRatedCoverImage.setImage(new Image("file:" + document.getDocumentCoverImageFilePath()));
        }
        topRatedTitle.setText(document.getDocumentTitle());
        topRatedAuthors.setText("By: " + String.join(",", document.getDocumentAuthors()));
    }

    private void recentOnSuccess(GetDocumentRecommendationController.@NonNull ViewModel viewModel) {
        val documentsList = viewModel.getDocumentRecommendation();
        if (documentsList.isEmpty()) {
            recentCoverImage.setImage(null);
            recentTitle.setText("No recent documents");
            recentAuthors.setText(null);
            return;
        }
        val document = documentsList.getFirst();
        if (document.getDocumentCoverImageFilePath().endsWith(".gif")) {
            recentCoverImage.setImage(new Image("/images/default/placeholder-book-cover.png"));
        } else {
            recentCoverImage.setImage(new Image("file:" + document.getDocumentCoverImageFilePath()));
        }
        recentTitle.setText(document.getDocumentTitle());
        recentAuthors.setText("By: " + String.join(",", document.getDocumentAuthors()));
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
    
    private @Unsigned int getNumberOfDocuments() {
        val viewModel = this.getStatisticsController.get();
        return viewModel.getNumberOfDocuments();
    }
}
