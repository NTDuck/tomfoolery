package org.tomfoolery.configurations.contexts.proxies;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.contexts.abc.ApplicationContext;
import org.tomfoolery.configurations.contexts.proxies.abc.ApplicationContextProxy;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.DocumentContent;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.infrastructures.utils.helpers.mockers.abc.EntityMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.documents.DocumentMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.relations.DocumentContentMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.AdministratorMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.PatronMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.StaffMocker;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(staticName = "of")
public final class MockingApplicationContextProxy implements ApplicationContextProxy {
    private static final @Unsigned int NUMBER_OF_DOCUMENTS = 0;
    private static final @Unsigned int NUMBER_OF_ADMINISTRATORS = 4;
    private static final @Unsigned int NUMBER_OF_PATRONS = 44;
    private static final @Unsigned int NUMBER_OF_STAFF = 4;
    private static final @Unsigned double PROPORTION_OF_DOCUMENTS_WITH_CONTENT = 0.9;

    private final @NonNull Executor executor = ForkJoinPool.commonPool();

    private final @NonNull DocumentMocker documentMocker = DocumentMocker.of();
    private final @NonNull AdministratorMocker administratorMocker = AdministratorMocker.of();
    private final @NonNull PatronMocker patronMocker = PatronMocker.of();
    private final @NonNull StaffMocker staffMocker = StaffMocker.of();
    private final @NonNull DocumentContentMocker documentContentMocker = DocumentContentMocker.of();

    @Override
    public @NonNull CompletableFuture<Void> intercept(@NonNull ApplicationContext applicationContext) {
        return CompletableFuture.allOf(
            this.populateDocumentRepository(applicationContext)
                .thenCompose(_ -> this.populateDocumentContentRepository(applicationContext)),
            this.populateAdministratorRepository(applicationContext),
            this.populatePatronRepository(applicationContext),
            this.populateStaffRepository(applicationContext)
        );
    }

    private @NonNull CompletableFuture<Void> populateDocumentRepository(@NonNull ApplicationContext applicationContext) {
        val documentRepository = applicationContext.getDocumentRepository();

        return this.populateRepositoryWithMockEntities(documentRepository, this.documentMocker, NUMBER_OF_DOCUMENTS);
    }

    private @NonNull CompletableFuture<Void> populateAdministratorRepository(@NonNull ApplicationContext applicationContext) {
        val administratorRepository = applicationContext.getAdministratorRepository();

        return this.populateRepositoryWithMockEntities(administratorRepository, this.administratorMocker, NUMBER_OF_ADMINISTRATORS);
    }

    private @NonNull CompletableFuture<Void> populatePatronRepository(@NonNull ApplicationContext applicationContext) {
        val patronRepository = applicationContext.getPatronRepository();

        return this.populateRepositoryWithMockEntities(patronRepository, this.patronMocker, NUMBER_OF_PATRONS);
    }

    private @NonNull CompletableFuture<Void> populateStaffRepository(@NonNull ApplicationContext applicationContext) {
        val staffRepository = applicationContext.getStaffRepository();

        return this.populateRepositoryWithMockEntities(staffRepository, this.staffMocker, NUMBER_OF_STAFF);
    }

    private @NonNull CompletableFuture<Void> populateDocumentContentRepository(@NonNull ApplicationContext applicationContext) {
        val documentContentRepository = applicationContext.getDocumentContentRepository();
        val documentRepository = applicationContext.getDocumentRepository();

        val documentContentIds = documentRepository.show().parallelStream()
            .map(Document::getId)
            .map(DocumentContent.Id::of)
            .collect(Collectors.toUnmodifiableList());

        val documentContentIdsSubset = getRandomSubset(documentContentIds, PROPORTION_OF_DOCUMENTS_WITH_CONTENT);

        return this.populateRepositoryWithMockEntities(documentContentRepository, documentContentIdsSubset, this.documentContentMocker::createMockEntityWithId);
    }

    private <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> @NonNull CompletableFuture<Void> populateRepositoryWithMockEntities(@NonNull BaseRepository<Entity, EntityId> repository, @NonNull EntityMocker<Entity, EntityId> entityMocker, @Unsigned int numberOfEntities) {
        val futures = IntStream.range(0, numberOfEntities)
            .parallel()
            .mapToObj(_ -> CompletableFuture.runAsync(() -> {
                EntityId entityId;

                do {
                    entityId = entityMocker.createMockEntityId();
                } while (repository.contains(entityId));

                val mockEntity = entityMocker.createMockEntityWithId(entityId);
                repository.save(mockEntity);
            }, this.executor))
            .collect(Collectors.toUnmodifiableList());

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> @NonNull CompletableFuture<Void> populateRepositoryWithMockEntities(@NonNull BaseRepository<Entity, EntityId> repository, @NonNull Collection<EntityId> entityIds, @NonNull Function<EntityId, Entity> entityByIdFunction) {
        val futures = entityIds.parallelStream()
            .map(entityId -> CompletableFuture
                .supplyAsync(() -> entityByIdFunction.apply(entityId), this.executor)
                .thenAccept(repository::save))
            .collect(Collectors.toUnmodifiableList());

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private static <T> @NonNull Collection<T> getRandomSubset(@NonNull Collection<T> sourceSet, @Unsigned double proportion) {
        assert 0 <= proportion && proportion <= 1;

        return IntStream.range(0, sourceSet.size()).parallel()
            .boxed()
            .sorted(Comparator.comparingDouble(_ -> Math.random()))
            .limit((long) (sourceSet.size() * proportion))
            .map(index -> sourceSet.parallelStream()
                .skip(index)
                .findFirst()
                .orElseThrow())
            .collect(Collectors.toList());
    }
}
