package org.tomfoolery.configurations.monolith.console.views.action.common.documents.recommendation;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.utils.helpers.EnumResolver;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.common.documents.recommendation.abc.GetDocumentRecommendationUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.recommendation.GetDocumentRecommendationController;

import java.util.stream.Collectors;

public final class GetDocumentRecommendationActionView extends UserActionView {
    private static final @NonNull String RECOMMENDATION_TYPE_PROMPT = String.format("recommendation type (%s)", EnumResolver.getEnumeratedNames(
        GetDocumentRecommendationController.RecommendationType.class, "%s (%d)", 0
    ).stream().collect(Collectors.joining(", ")));

    private final @NonNull GetDocumentRecommendationController getDocumentRecommendationController;

    public static @NonNull GetDocumentRecommendationActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentRecommendationActionView(ioProvider, documentRepository, documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentRecommendationActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.getDocumentRecommendationController = GetDocumentRecommendationController.of(documentRepository, documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.getDocumentRecommendationController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (RecommendationTypeIndexInvalidException exception) {
            this.onException(exception);

        } catch (GetDocumentRecommendationUseCase.AuthenticationTokenNotFoundException | GetDocumentRecommendationUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        }
    }

    private GetDocumentRecommendationController.@NonNull RequestObject collectRequestObject() throws RecommendationTypeIndexInvalidException {
        val recommendationType = this.collectRecommendationType();

        return GetDocumentRecommendationController.RequestObject.of(recommendationType);
    }

    private GetDocumentRecommendationController.@NonNull RecommendationType collectRecommendationType() throws RecommendationTypeIndexInvalidException {
        val rawRecommendationTypeIndex = this.ioProvider.readLine(Message.Format.PROMPT, RECOMMENDATION_TYPE_PROMPT);

        try {
            val recommendationTypeIndex = Integer.parseUnsignedInt(rawRecommendationTypeIndex);
            val recommendationType = GetDocumentRecommendationController.RecommendationType.values()[recommendationTypeIndex];

            return recommendationType;

        } catch (Exception exception) {
            throw new RecommendationTypeIndexInvalidException();
        }
    }

    private void onSuccess(GetDocumentRecommendationController.@NonNull ViewModel viewModel) {
        this.nextViewClass = cachedViewClass;

        this.displayViewModel(viewModel);
    }

    private void displayViewModel(GetDocumentRecommendationController.@NonNull ViewModel viewModel) {
        this.ioProvider.writeLine("Showing recommended documents:");

        viewModel.getDocumentRecommendation()
            .forEach(document -> {
                this.ioProvider.writeLine("- [%s] %s", document.getDocumentISBN_10(), document.getDocumentTitle());
            });
    }

    private static class RecommendationTypeIndexInvalidException extends Exception {}
}
