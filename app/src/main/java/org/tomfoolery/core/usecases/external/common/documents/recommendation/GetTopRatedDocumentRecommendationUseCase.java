package org.tomfoolery.core.usecases.external.common.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.external.common.documents.recommendation.abc.GetDocumentRecommendationUseCase;

import java.util.List;
import java.util.function.Supplier;

public final class GetTopRatedDocumentRecommendationUseCase extends GetDocumentRecommendationUseCase {
    public static @NonNull GetTopRatedDocumentRecommendationUseCase of(@NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetTopRatedDocumentRecommendationUseCase(documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetTopRatedDocumentRecommendationUseCase(@NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super( documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    protected @NonNull Supplier<List<Document>> getDocumentRecommendationSupplier() {
        return this.documentRecommendationGenerator::getTopRatedDocumentRecommendation;
    }
}