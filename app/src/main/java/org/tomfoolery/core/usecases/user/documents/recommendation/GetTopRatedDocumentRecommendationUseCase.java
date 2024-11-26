package org.tomfoolery.core.usecases.user.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.usecases.user.documents.recommendation.abc.GetDocumentRecommendationUseCase;

import java.util.List;
import java.util.function.Supplier;

public final class GetTopRatedDocumentRecommendationUseCase extends GetDocumentRecommendationUseCase {
    public static @NonNull GetTopRatedDocumentRecommendationUseCase of(@NonNull DocumentRepository documentRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetTopRatedDocumentRecommendationUseCase(documentRepository, documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetTopRatedDocumentRecommendationUseCase(@NonNull DocumentRepository documentRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(documentRepository, documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    protected @NonNull Supplier<List<FragmentaryDocument>> getDocumentRecommendationSupplier() {
        return this.documentRecommendationGenerator::getTopRatedDocumentRecommendation;
    }
}