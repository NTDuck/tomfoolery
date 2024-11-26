package org.tomfoolery.infrastructures.dataproviders.repositories.hybrid.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class BaseHybridRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> implements BaseRepository<Entity, EntityId> {
    protected final @NonNull BaseRepository<Entity, EntityId> persistenceRepository;
    protected final @NonNull List<BaseRepository<Entity, EntityId>> retrievalRepositories;

    public static <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> @NonNull BaseHybridRepository<Entity, EntityId> of(@NonNull BaseRepository<Entity, EntityId> persistenceRepository, @NonNull List<BaseRepository<Entity, EntityId>> retrievalRepositories) {
        return new BaseHybridRepository<>(persistenceRepository, retrievalRepositories);
    }

    protected BaseHybridRepository(@NonNull BaseRepository<Entity, EntityId> persistenceRepository, @NonNull List<BaseRepository<Entity, EntityId>> retrievalRepositories) {
        this.persistenceRepository = persistenceRepository;
        this.retrievalRepositories = retrievalRepositories;
    }

    @Override
    public void save(@NonNull Entity entity) {
        this.persistenceRepository.save(entity);
    }

    @Override
    public void delete(@NonNull EntityId entityId) {
        this.persistenceRepository.delete(entityId);
    }

    @Override
    public @Nullable Entity getById(@NonNull EntityId entityId) {
        val persistedEntity = this.persistenceRepository.getById(entityId);

        if (persistedEntity != null)
            return persistedEntity;

        val retrievedEntity = this.getByIdFromAnyRetrievalRepository(entityId);

        if (retrievedEntity != null)
            CompletableFuture.runAsync(() -> this.save(retrievedEntity));

        return retrievedEntity;
    }

    @Override
    public @NonNull List<Entity> show() {
        return this.persistenceRepository.show();
    }

    @Override
    public boolean contains(@NonNull EntityId entityId) {
        val persistenceContains = this.persistenceRepository.contains(entityId);

        if (persistenceContains)
            return true;

        val retrievalContains = this.containsFromAnyRetrievalRepository(entityId);

        if (retrievalContains) {
            val retrievedEntity = this.getByIdFromAnyRetrievalRepository(entityId);
            assert retrievedEntity != null;

            CompletableFuture.runAsync(() -> this.save(retrievedEntity));
        }

        return retrievalContains;
    }

    private @Nullable Entity getByIdFromAnyRetrievalRepository(@NonNull EntityId entityId) {
        return this.applyOnAnyRetrievalRepository(repository -> repository.getById(entityId));
    }

    private boolean containsFromAnyRetrievalRepository(@Nullable EntityId entityId) {
        val contains = this.applyOnAnyRetrievalRepository(repository -> repository.contains(entityId));
        return contains != null ? contains : false;
    }

    private <T> @Nullable T applyOnAnyRetrievalRepository(@NonNull Function<BaseRepository<Entity, EntityId>, T> function) {
        return this.retrievalRepositories.parallelStream()
            .map(function)
            .findAny()
            .orElse(null);
    }
}
