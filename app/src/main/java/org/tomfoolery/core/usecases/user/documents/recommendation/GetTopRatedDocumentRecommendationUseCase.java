package org.tomfoolery.core.usecases.user.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.recommendation.DocumentRecommendationRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.user.abc.GetScheduledDocumentRecommendationUseCase;

import java.util.Collection;
import java.util.function.Supplier;

public final class GetTopRatedDocumentRecommendationUseCase extends GetScheduledDocumentRecommendationUseCase {
    public static @NonNull GetTopRatedDocumentRecommendationUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull DocumentRecommendationRepository documentRecommendationRepository) {
        return new GetTopRatedDocumentRecommendationUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRecommendationGenerator, documentRecommendationRepository);
    }

    private GetTopRatedDocumentRecommendationUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull DocumentRecommendationRepository documentRecommendationRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository, documentRecommendationGenerator, documentRecommendationRepository);
    }

    @Override
    protected @NonNull Supplier<Collection<Document>> getDocumentRecommendationSupplier() {
        return this.documentRecommendationRepository::getTopRatedDocumentRecommendation;
    }
}