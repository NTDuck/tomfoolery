package org.tomfoolery.core.usecases.user.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.recommendation.DocumentRecommendationRepository;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableSupplier;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class GetScheduledDocumentRecommendationUseCase extends AuthenticatedUserUseCase implements ThrowableSupplier<GetScheduledDocumentRecommendationUseCase.Response> {
    private static final @NonNull Duration GENERATION_INTERVAL = Duration.ofDays(1);

    private final @NonNull DocumentRecommendationGenerator documentRecommendationGenerator;
    protected final @NonNull DocumentRecommendationRepository documentRecommendationRepository;

    protected GetScheduledDocumentRecommendationUseCase(@NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull DocumentRecommendationRepository documentRecommendationRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRecommendationGenerator = documentRecommendationGenerator;
        this.documentRecommendationRepository = documentRecommendationRepository;
    }

    protected abstract @NonNull Supplier<List<FragmentaryDocument>> getDocumentRecommendationSupplier();

    @Override
    public @NonNull Response get() throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException {
        val userAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(userAuthenticationToken);

        if (this.isGenerationIntervalElapsed())
            this.regenerateDocumentRecommendations();   // Non-blocking

        val documentRecommendationSupplier = this.getDocumentRecommendationSupplier();
        val documentRecommendation = documentRecommendationSupplier.get();

        return Response.of(documentRecommendation);
    }

    private boolean isGenerationIntervalElapsed() {
        val lastGeneratedTimestamp = this.documentRecommendationGenerator.getLastGeneratedTimestamp();
        val currentTimestamp = Instant.now();

        return Duration.between(lastGeneratedTimestamp, currentTimestamp)
            .compareTo(GENERATION_INTERVAL) > 0;
    }

    private void regenerateDocumentRecommendations() {
        CompletableFuture.runAsync(() -> {
            val futureOfLatestDocumentRecommendation = CompletableFuture.supplyAsync(this.documentRecommendationGenerator::generateLatestDocumentRecommendation);
            val futureOfPopularDocumentRecommendation = CompletableFuture.supplyAsync(this.documentRecommendationGenerator::generatePopularDocumentRecommendation);
            val futureOfTopRatedDocumentRecommendation = CompletableFuture.supplyAsync(this.documentRecommendationGenerator::generateTopRatedDocumentRecommendation);

            CompletableFuture.allOf(
                futureOfLatestDocumentRecommendation,
                futureOfTopRatedDocumentRecommendation,
                futureOfPopularDocumentRecommendation
            ).thenRun(() -> {
                this.documentRecommendationRepository.saveLatestDocumentRecommendation(futureOfLatestDocumentRecommendation.join());
                this.documentRecommendationRepository.savePopularDocumentRecommendation(futureOfPopularDocumentRecommendation.join());
                this.documentRecommendationRepository.saveTopRatedDocumentRecommendation(futureOfTopRatedDocumentRecommendation.join());

                val currentTimestamp = Instant.now();
                this.documentRecommendationGenerator.setLastGeneratedTimestamp(currentTimestamp);
            });
        });
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull List<FragmentaryDocument> documentRecommendation;
    }
}
