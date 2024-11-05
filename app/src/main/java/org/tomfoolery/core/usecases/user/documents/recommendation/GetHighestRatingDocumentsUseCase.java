package org.tomfoolery.core.usecases.user.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.user.abc.GetRecommendedDocumentsUseCase;

import java.util.Collection;

public final class GetHighestRatingDocumentsUseCase extends GetRecommendedDocumentsUseCase {
    public static @NonNull GetHighestRatingDocumentsUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator) {
        return new GetHighestRatingDocumentsUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRecommendationGenerator);
    }

    private GetHighestRatingDocumentsUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator) {
        super(authenticationTokenGenerator, authenticationTokenRepository, documentRecommendationGenerator);
    }

    @Override
    protected @NonNull Collection<Document> getRecommendedDocuments() {
        return this.documentRecommendationGenerator.getHighestRatingDocuments();
    }
}