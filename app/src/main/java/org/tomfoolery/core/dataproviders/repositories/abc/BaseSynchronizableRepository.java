package org.tomfoolery.core.dataproviders.repositories.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.time.Instant;
import java.util.List;

public interface BaseSynchronizableRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseRepository<Entity, EntityId> {
    @NonNull List<Entity> getSavedEntitiesSince(@NonNull Instant timestamp);
    @NonNull List<EntityId> getDeletedEntityIdsSince(@NonNull Instant timestamp);
}
