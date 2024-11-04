package org.tomfoolery.core.usecases.user.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.documents.DocumentRecommendationGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableSupplier;

import java.util.Collection;

public abstract class GetRecommendedDocumentsUseCase extends AuthenticatedUserUseCase implements ThrowableSupplier<GetRecommendedDocumentsUseCase.Response> {
    protected final @NonNull DocumentRecommendationGenerator documentRecommendationGenerator;

    protected GetRecommendedDocumentsUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        this.documentRecommendationGenerator = documentRecommendationGenerator;
    }

    protected abstract @NonNull Collection<Document> getRecommendedDocuments();

    @Override
    public @NonNull Response get() throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException {
        val userAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(userAuthenticationToken);

        val recommendedDocuments = getRecommendedDocuments();
        val recommendedDocumentPreviews = getRecommendedDocumentPreviews(recommendedDocuments);

        return Response.of(recommendedDocumentPreviews);
    }

    private static @NonNull Collection<Document.Preview> getRecommendedDocumentPreviews(@NonNull Collection<Document> recommendedDocuments) {
        return recommendedDocuments.stream()
            .map(Document.Preview::of)
            .toList();
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Collection<Document.Preview> recommendedDocumentPreviews;
    }
}
