package org.tomfoolery.core.usecases.user.documents.recommendation.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.DocumentWithoutContent;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableSupplier;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class GetDocumentRecommendationUseCase extends AuthenticatedUserUseCase implements ThrowableSupplier<GetDocumentRecommendationUseCase.Response> {
    private static final @NonNull Duration SYNCHRONIZATION_INTERVAL = Duration.ofDays(1);

    private final @NonNull DocumentRepository documentRepository;
    protected final @NonNull DocumentRecommendationGenerator documentRecommendationGenerator;

    protected GetDocumentRecommendationUseCase(@NonNull DocumentRepository documentRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.documentRecommendationGenerator = documentRecommendationGenerator;
    }

    protected abstract @NonNull Supplier<List<DocumentWithoutContent>> getDocumentRecommendationSupplier();

    @Override
    public @NonNull Response get() throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException {
        val userAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(userAuthenticationToken);

        this.synchronizeGeneratorIfIntervalIsElapsed();

        val documentRecommendationSupplier = this.getDocumentRecommendationSupplier();
        val documentRecommendation = documentRecommendationSupplier.get();

        return Response.of(documentRecommendation);
    }

    private void synchronizeGeneratorIfIntervalIsElapsed() {
        if (!this.documentRecommendationGenerator.isSynchronizedIntervalElapsed(SYNCHRONIZATION_INTERVAL))
            return;

        CompletableFuture.runAsync(() -> this.documentRecommendationGenerator.synchronizeWithRepository(this.documentRepository));
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull List<DocumentWithoutContent> documentRecommendation;
    }
}
