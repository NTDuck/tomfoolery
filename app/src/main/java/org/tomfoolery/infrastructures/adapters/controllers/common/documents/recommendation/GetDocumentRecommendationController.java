package org.tomfoolery.infrastructures.adapters.controllers.common.documents.recommendation;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.common.documents.recommendation.abc.GetDocumentRecommendationUseCase;
import org.tomfoolery.core.usecases.common.documents.recommendation.GetLatestDocumentRecommendationUseCase;
import org.tomfoolery.core.usecases.common.documents.recommendation.GetTopRatedDocumentRecommendationUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.retrieval.GetDocumentByIdController;

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
            RecommendationType.TOP_RATED, GetTopRatedDocumentRecommendationUseCase::of
        );

        this.getScheduledDocumentRecommendationUseCasesByRecommendationTypes = getDocumentRecommendationUseCaseInitializersByRecommendationTypes.entrySet().parallelStream()
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey,
                entry -> entry.getValue().apply(documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository)
            ));
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws GetDocumentRecommendationUseCase.AuthenticationTokenNotFoundException, GetDocumentRecommendationUseCase.AuthenticationTokenInvalidException {
        val recommendationType = requestObject.getRecommendationType();
        val getScheduledDocumentRecommendationUseCase = this.getScheduledDocumentRecommendationUseCasesByRecommendationTypes.get(recommendationType);

        val responseModel = getScheduledDocumentRecommendationUseCase.get();
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(GetDocumentRecommendationUseCase.@NonNull Response responseModel) {
        val documentRecommendation = responseModel.getDocumentRecommendation().parallelStream()
            .map(GetDocumentByIdController.ViewModel::of)
            .collect(Collectors.toUnmodifiableList());

        return ViewModel.of(documentRecommendation);
    }
    
    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull RecommendationType recommendationType;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull List<GetDocumentByIdController.ViewModel> documentRecommendation;
    }

    public enum RecommendationType {
        LATEST, TOP_RATED,
    }

    @FunctionalInterface
    private interface GetDocumentRecommendationUseCaseInitializer {
        @NonNull GetDocumentRecommendationUseCase apply(@NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository);
    }
}
