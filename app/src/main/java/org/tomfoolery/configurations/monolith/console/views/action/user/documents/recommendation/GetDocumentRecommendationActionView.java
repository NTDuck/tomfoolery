package org.tomfoolery.configurations.monolith.console.views.action.user.documents.recommendation;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.utils.helpers.EnumResolver;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.documents.retrieval.recommendation.abc.GetDocumentRecommendationUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.GetDocumentRecommendationController;

import java.util.stream.Collectors;

public final class GetDocumentRecommendationActionView extends UserActionView {
    private static final @NonNull String RECOMMENDATION_TYPE_PROMPT = String.format("recommendation type (%s)", EnumResolver.getEnumeratedNames(
            GetDocumentRecommendationController.RecommendationType.class, "%s %d", 0
    ).stream().collect(Collectors.joining(", ")));

    private final @NonNull GetDocumentRecommendationController controller;

    public static @NonNull GetDocumentRecommendationActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentRecommendationActionView(ioProvider, documentRepository, documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentRecommendationActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = GetDocumentRecommendationController.of(documentRepository, documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.controller.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (RecommendationTypeIndexInvalidException exception) {
            this.onRecommendationTypeIndexInvalidException();

        } catch (GetDocumentRecommendationUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (GetDocumentRecommendationUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
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
            .forEach(fragmentaryDocument -> {
                val ISBN = fragmentaryDocument.getISBN();
                val documentTitle = fragmentaryDocument.getDocumentTitle();

                this.ioProvider.writeLine("- (%s) %s", ISBN, documentTitle);
            });
    }

    private void onRecommendationTypeIndexInvalidException() {
        this.nextViewClass = cachedViewClass;

        this.ioProvider.writeLine(Message.Format.ERROR, "Recommendation type must be a valid number");
    }

    private static class RecommendationTypeIndexInvalidException extends Exception {}
}
