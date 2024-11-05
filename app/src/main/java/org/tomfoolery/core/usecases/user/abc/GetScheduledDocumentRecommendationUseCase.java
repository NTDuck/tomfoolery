package org.tomfoolery.core.usecases.user.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.recommendation.DocumentRecommendationRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableSupplier;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class GetScheduledDocumentRecommendationUseCase extends AuthenticatedUserUseCase implements ThrowableSupplier<GetScheduledDocumentRecommendationUseCase.Response> {
    private static final @NonNull Duration GENERATION_INTERVAL = Duration.ofDays(1);

    private final @NonNull DocumentRecommendationGenerator documentRecommendationGenerator;
    protected final @NonNull DocumentRecommendationRepository documentRecommendationRepository;

    protected GetScheduledDocumentRecommendationUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull DocumentRecommendationRepository documentRecommendationRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRecommendationGenerator = documentRecommendationGenerator;
        this.documentRecommendationRepository = documentRecommendationRepository;
    }

    protected abstract @NonNull Supplier<Collection<Document>> getDocumentRecommendationSupplier();

    @Override
    public @NonNull Response get() throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException {
        val userAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(userAuthenticationToken);

        if (isGenerationIntervalElapsed())
            regenerateDocumentRecommendations();   // Non-blocking

        val documentRecommendationSupplier = getDocumentRecommendationSupplier();
        val documentRecommendation = documentRecommendationSupplier.get();
        val documentRecommendationPreview = getDocumentRecommendationPreviewFromDocumentRecommendation(documentRecommendation);

        return Response.of(documentRecommendationPreview);
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

    private static @NonNull Collection<Document.Preview> getDocumentRecommendationPreviewFromDocumentRecommendation(@NonNull Collection<Document> documentRecommendation) {
        return documentRecommendation.stream()
            .map(Document.Preview::of)
            .collect(Collectors.toUnmodifiableList());
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Collection<Document.Preview> documentRecommendationPreview;
    }
}
