package org.tomfoolery.infrastructures.adapters.controllers.user.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.recommendation.DocumentRecommendationRepository;
import org.tomfoolery.core.usecases.user.documents.recommendation.abc.GetDocumentRecommendationUseCase;
import org.tomfoolery.core.usecases.user.documents.recommendation.GetLatestDocumentRecommendationUseCase;
import org.tomfoolery.core.usecases.user.documents.recommendation.GetPopularDocumentRecommendationUseCase;
import org.tomfoolery.core.usecases.user.documents.recommendation.GetTopRatedDocumentRecommendationUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.utils.dataclasses.ViewableFragmentaryDocument;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class GetDocumentRecommendationController implements ThrowableFunction<GetDocumentRecommendationController.RequestObject, GetDocumentRecommendationController.ViewModel> {
    private final @NonNull Map<RecommendationType, GetDocumentRecommendationUseCase> getScheduledDocumentRecommendationUseCasesByRecommendationTypes;

    public static @NonNull GetDocumentRecommendationController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull DocumentRecommendationRepository documentRecommendationRepository) {
        return new GetDocumentRecommendationController(authenticationTokenGenerator, authenticationTokenRepository, documentRecommendationGenerator, documentRecommendationRepository);
    }

    private GetDocumentRecommendationController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull DocumentRecommendationRepository documentRecommendationRepository) {
        this.getScheduledDocumentRecommendationUseCasesByRecommendationTypes = Map.of(
            RecommendationType.LATEST, GetLatestDocumentRecommendationUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRecommendationGenerator, documentRecommendationRepository),
            RecommendationType.POPULAR, GetPopularDocumentRecommendationUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRecommendationGenerator, documentRecommendationRepository),
            RecommendationType.TOP_RATED, GetTopRatedDocumentRecommendationUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRecommendationGenerator, documentRecommendationRepository)
        );
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws GetDocumentRecommendationUseCase.AuthenticationTokenNotFoundException, GetDocumentRecommendationUseCase.AuthenticationTokenInvalidException {
        val recommendationType = requestObject.getRecommendationType();
        val getScheduledDocumentRecommendationUseCase = this.getScheduledDocumentRecommendationUseCasesByRecommendationTypes.get(recommendationType);

        val responseModel = getScheduledDocumentRecommendationUseCase.get();
        val viewModel = ViewModel.fromResponseModel(responseModel);

        return viewModel;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull RecommendationType recommendationType;
    }

    @Value
    public static class ViewModel {
        @NonNull List<ViewableFragmentaryDocument> documentRecommendation;

        public static @NonNull ViewModel fromResponseModel(GetDocumentRecommendationUseCase.@NonNull Response responseModel) {
            val documentRecommendation = responseModel.getDocumentRecommendation();
            val viewableDocumentRecommendation = documentRecommendation.parallelStream()
                .map(ViewableFragmentaryDocument::of)
                .collect(Collectors.toUnmodifiableList());

            return new ViewModel(viewableDocumentRecommendation);
        }
    }

    public enum RecommendationType {
        LATEST, POPULAR, TOP_RATED,
    }
}
