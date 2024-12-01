package org.tomfoolery.core.usecases.shared.documents.recommendation.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableSupplier;

import java.util.List;
import java.util.function.Supplier;

public abstract class GetDocumentRecommendationUseCase extends AuthenticatedUserUseCase implements ThrowableSupplier<GetDocumentRecommendationUseCase.Response> {
    protected final @NonNull DocumentRecommendationGenerator documentRecommendationGenerator;

    protected GetDocumentRecommendationUseCase(@NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRecommendationGenerator = documentRecommendationGenerator;
    }

    protected abstract @NonNull Supplier<List<DocumentWithoutContent>> getDocumentRecommendationSupplier();

    @Override
    public @NonNull Response get() throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException {
        val userAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(userAuthenticationToken);

        val documentRecommendationSupplier = this.getDocumentRecommendationSupplier();
        val documentRecommendation = documentRecommendationSupplier.get();

        return Response.of(documentRecommendation);
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull List<DocumentWithoutContent> documentRecommendation;
    }
}
