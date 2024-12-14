package org.tomfoolery.core.dataproviders.repositories.aggregates.hybrids.abc;

import lombok.Locked;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRetrievalRepository;
import org.tomfoolery.core.dataproviders.repositories.aggregates.abc.BaseAggregateRepository;
import org.tomfoolery.core.utils.contracts.ddd;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A Hybrid Repository allows invocation of fallback repositories in the retrieval of entities.
 */
public class BaseHybridRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseAggregateRepository<Entity, EntityId> {
    protected final @NonNull List<? extends BaseRepository<Entity, EntityId>> retrievedRepositories;

    protected BaseHybridRepository(@NonNull BaseRepository<Entity, EntityId> persistenceRepository, @NonNull List<? extends BaseRetrievalRepository<Entity, EntityId>> retrievalRepositories) {
        super(persistenceRepository);

        this.retrievedRepositories = retrievalRepositories;
        assert !this.retrievedRepositories.contains(this.repository);
    }

    @Override
    @Locked.Read
    public @Nullable Entity getById(@NonNull EntityId entityId) {
        val persistedEntity = this.repository.getById(entityId);

        if (persistedEntity != null)
            return persistedEntity;

        val retrievedEntity = this.retrievedRepositories.parallelStream()
            .map(repository -> repository.getById(entityId))
            .filter(Objects::nonNull)
            .findAny()
            .orElse(null);

        if (retrievedEntity != null)
            CompletableFuture.runAsync(() -> this.repository.save(retrievedEntity));

        return retrievedEntity;
    }

    @Override
    @Locked.Read
    public boolean contains(@NonNull EntityId entityId) {
        val persistedEntityExists = this.repository.contains(entityId);

        if (persistedEntityExists)
            return true;

        val retrievalRepositoryWithEntity = this.retrievedRepositories.parallelStream()
            .filter(repository -> repository.contains(entityId))
            .findAny()
            .orElse(null);

        if (retrievalRepositoryWithEntity != null) {
            CompletableFuture.runAsync(() -> {
                val retrievedEntity = retrievalRepositoryWithEntity.getById(entityId);
                assert retrievedEntity != null;

                this.repository.save(retrievedEntity);
            });
        }

        return retrievalRepositoryWithEntity != null;
    }
}
