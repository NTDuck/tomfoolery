package org.tomfoolery.core.dataproviders.repositories.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.time.Instant;
import java.util.Set;

/**
 * Only the most recent state of an entity is retained.
 * For example, given timestamp {@code T},
 * entity {@code E} is saved at {@code T+1},
 * then deleted at {@code T+2}.
 * Calling {@code getSavedEntitiesSince(T)} would yield {@code []},
 * and {@code getDeletedEntitiesSince(T)} would yield {@code [E]}.
 * (because the deletion happens more recently)
 */
public interface BaseSynchronizableRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseRepository<Entity, EntityId> {
    @NonNull Set<Entity> getSavedEntitiesSince(@NonNull Instant fromTimestamp);
    @NonNull Set<Entity> getDeletedEntitiesSince(@NonNull Instant fromTimestamp);
}
