package org.tomfoolery.core.dataproviders.aggregates;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.usecases.staff.documents.persistence.AddDocumentUseCase;
import org.tomfoolery.core.utils.contracts.ddd;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BaseHybridRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> implements BaseRepository<Entity, EntityId> {
    protected final @NonNull List<? extends BaseRepository<Entity, EntityId>> persistenceRepositories;
    protected final @NonNull List<? extends BaseRepository<Entity, EntityId>> retrievalRepositories;

    protected BaseHybridRepository(@NonNull List<? extends BaseRepository<Entity, EntityId>> persistenceRepositories, @NonNull List<? extends BaseRepository<Entity, EntityId>> retrievalRepositories) {
        this.persistenceRepositories = persistenceRepositories;
        this.retrievalRepositories = retrievalRepositories;
    }

    @Override
    public void save(@NonNull Entity entity) {
        accept(this.persistenceRepositories, repository -> repository.save(entity));
    }

    @Override
    public void delete(@NonNull EntityId entityId) {
        accept(this.persistenceRepositories, repository -> repository.delete(entityId));
    }

    @Override
    public @Nullable Entity getById(@NonNull EntityId entityId) {
        val entityFromPersistenceRepositories = getById(this.persistenceRepositories, entityId);

        if (entityFromPersistenceRepositories != null)
            return entityFromPersistenceRepositories;

        val entityFromRetrievalRepositories = getById(this.retrievalRepositories, entityId);

        if (entityFromRetrievalRepositories != null)
            CompletableFuture.runAsync(() -> this.save(entityFromRetrievalRepositories));

        return entityFromRetrievalRepositories;
    }

    @Override
    public @NonNull List<Entity> show() {
        return this.persistenceRepositories.stream()
            .flatMap(repository -> repository.show().stream())
            .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Original implementation followed {@link BaseHybridRepository#getById(ddd.EntityId)},
     * however was refactored after several use cases (e.g. {@link AddDocumentUseCase})
     * reported undefined behaviour.
     */
    @Override
    public boolean contains(@NonNull EntityId entityId) {
        // val containsFromPersistenceRepositories = contains(this.persistenceRepositories, entityId);
        //
        // if (containsFromPersistenceRepositories)
        //     return true;
        //
        // val containsFromRetrievalRepositories = contains(this.retrievalRepositories, entityId);
        //
        // if (containsFromRetrievalRepositories) {
        //     CompletableFuture.runAsync(() -> {
        //         val retrievedEntity = getById(retrievalRepositories, entityId);
        //
        //         if (retrievedEntity != null)
        //             this.save(retrievedEntity);
        //     });
        // }
        //
        // return containsFromRetrievalRepositories;

        return contains(this.persistenceRepositories, entityId);
    }

    private static <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> @Nullable Entity getById(@NonNull List<? extends BaseRepository<Entity, EntityId>> repositories, @NonNull EntityId entityId) {
        return apply(repositories, repository -> repository.getById(entityId));
    }

    private static <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> boolean contains(@NonNull List<? extends BaseRepository<Entity, EntityId>> repositories, @NonNull EntityId entityId) {
        val contains = apply(repositories, repository -> repository.contains(entityId));
        return contains != null ? contains : false;
    }

    private static <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId, T> @Nullable T apply(@NonNull List<? extends BaseRepository<Entity, EntityId>> repositories, @NonNull Function<BaseRepository<Entity, EntityId>, T> function) {
        return repositories.parallelStream()
            .map(function)
            .filter(Objects::nonNull)
            .findAny()
            .orElse(null);
    }

    private static <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId, T> void accept(@NonNull List<? extends BaseRepository<Entity, EntityId>> repositories, @NonNull Consumer<BaseRepository<Entity, EntityId>> consumer) {
        repositories.parallelStream()
            .forEach(consumer);
    }
}
