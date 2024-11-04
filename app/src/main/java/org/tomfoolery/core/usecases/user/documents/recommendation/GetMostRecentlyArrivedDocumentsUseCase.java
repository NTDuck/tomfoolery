package org.tomfoolery.core.usecases.user.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.documents.DocumentRecommendationGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.user.abc.GetRecommendedDocumentsUseCase;

import java.util.Collection;

public final class GetMostRecentlyArrivedDocumentsUseCase extends GetRecommendedDocumentsUseCase {
    public static @NonNull GetMostRecentlyArrivedDocumentsUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator) {
        return new GetMostRecentlyArrivedDocumentsUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRecommendationGenerator);
    }

    private GetMostRecentlyArrivedDocumentsUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator) {
        super(authenticationTokenGenerator, authenticationTokenRepository, documentRecommendationGenerator);
    }

    @Override
    protected @NonNull Collection<Document> getRecommendedDocuments() {
        return this.documentRecommendationGenerator.getMostRecentlyArrivedDocuments();
    }
}