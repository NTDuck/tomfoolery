package org.tomfoolery.infrastructures.dataproviders.repositories.hybrid.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseSynchronizableRepository;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public class BaseHybridSynchronizableRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseHybridRepository<Entity, EntityId> implements BaseSynchronizableRepository<Entity, EntityId> {
    public static <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> @NonNull BaseHybridSynchronizableRepository<Entity, EntityId> of(@NonNull BaseSynchronizableRepository<Entity, EntityId> synchronizablePersistenceRepository, @NonNull List<BaseRepository<Entity, EntityId>> retrievalRepositories) {
        return new BaseHybridSynchronizableRepository<>(synchronizablePersistenceRepository, retrievalRepositories);
    }

    protected BaseHybridSynchronizableRepository(@NonNull BaseSynchronizableRepository<Entity, EntityId> synchronizablePersistenceRepository, @NonNull List<BaseRepository<Entity, EntityId>> retrievalRepositories) {
        super(synchronizablePersistenceRepository, retrievalRepositories);
    }

    @Override
    public @NonNull Set<Entity> getSavedEntitiesSince(@NonNull Instant fromTimestamp) {
        val synchronizablePersistenceRepository = this.getSynchronizablePersistenceRepository();
        return synchronizablePersistenceRepository.getSavedEntitiesSince(fromTimestamp);
    }

    @Override
    public @NonNull Set<Entity> getDeletedEntitiesSince(@NonNull Instant fromTimestamp) {
        val synchronizablePersistenceRepository = this.getSynchronizablePersistenceRepository();
        return synchronizablePersistenceRepository.getDeletedEntitiesSince(fromTimestamp);
    }

    private @NonNull BaseSynchronizableRepository<Entity, EntityId> getSynchronizablePersistenceRepository() {
        return (BaseSynchronizableRepository<Entity, EntityId>) this.persistenceRepository;
    }
}