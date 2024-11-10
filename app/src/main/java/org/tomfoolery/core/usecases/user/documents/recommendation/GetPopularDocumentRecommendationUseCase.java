package org.tomfoolery.core.usecases.user.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.recommendation.DocumentRecommendationRepository;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.usecases.user.abc.GetScheduledDocumentRecommendationUseCase;

import java.util.List;
import java.util.function.Supplier;

public final class GetPopularDocumentRecommendationUseCase extends GetScheduledDocumentRecommendationUseCase {
    public static @NonNull GetPopularDocumentRecommendationUseCase of(@NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull DocumentRecommendationRepository documentRecommendationRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetPopularDocumentRecommendationUseCase(documentRecommendationGenerator, documentRecommendationRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetPopularDocumentRecommendationUseCase(@NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull DocumentRecommendationRepository documentRecommendationRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(documentRecommendationGenerator, documentRecommendationRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    protected @NonNull Supplier<List<FragmentaryDocument>> getDocumentRecommendationSupplier() {
        return this.documentRecommendationRepository::getPopularDocumentRecommendation;
    }
}