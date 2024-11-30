package org.tomfoolery.infrastructures.adapters.controllers.user.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.documents.retrieval.recommendation.abc.GetDocumentRecommendationUseCase;
import org.tomfoolery.core.usecases.documents.retrieval.recommendation.GetLatestDocumentRecommendationUseCase;
import org.tomfoolery.core.usecases.documents.retrieval.recommendation.GetPopularDocumentRecommendationUseCase;
import org.tomfoolery.core.usecases.documents.retrieval.recommendation.GetTopRatedDocumentRecommendationUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.utils.dataclasses.ViewableFragmentaryDocument;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class GetDocumentRecommendationController implements ThrowableFunction<GetDocumentRecommendationController.RequestObject, GetDocumentRecommendationController.ViewModel> {
    private final @NonNull Map<RecommendationType, GetDocumentRecommendationUseCase> getScheduledDocumentRecommendationUseCasesByRecommendationTypes;

    public static @NonNull GetDocumentRecommendationController of(@NonNull DocumentRepository documentRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentRecommendationController(documentRepository, documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentRecommendationController(@NonNull DocumentRepository documentRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        Map<RecommendationType, GetDocumentRecommendationUseCaseInitializer> getDocumentRecommendationUseCaseInitializersByRecommendationTypes = Map.of(
            RecommendationType.LATEST, GetLatestDocumentRecommendationUseCase::of,
            RecommendationType.POPULAR, GetPopularDocumentRecommendationUseCase::of,
            RecommendationType.TOP_RATED, GetTopRatedDocumentRecommendationUseCase::of
        );

        this.getScheduledDocumentRecommendationUseCasesByRecommendationTypes = getDocumentRecommendationUseCaseInitializersByRecommendationTypes.entrySet().parallelStream()
            .collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                entry -> entry.getValue().apply(documentRepository, documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository)
            ));
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

    @FunctionalInterface
    private interface GetDocumentRecommendationUseCaseInitializer {
        @NonNull GetDocumentRecommendationUseCase apply(@NonNull DocumentRepository documentRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository);
    }
}
