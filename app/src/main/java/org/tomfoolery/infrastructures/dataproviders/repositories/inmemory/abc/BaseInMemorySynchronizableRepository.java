package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseSynchronizableRepository;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.stream.Collectors;

public class BaseInMemorySynchronizableRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseInMemoryRepository<Entity, EntityId> implements BaseSynchronizableRepository<Entity, EntityId> {
    private static final @Unsigned long LIMIT = (long) Math.pow(2, 22);

    private final @NonNull Deque<EntityAndTimestamp> entityAndTimestamps = new ArrayDeque<>();
    private final @NonNull Deque<EntityIdAndTimestamp> entityIdAndTimestamps = new ArrayDeque<>();

    @Override
    public void save(@NonNull Entity entity) {
        super.save(entity);

        val currentTimestamp = getCurrentTimestamp();
        this.entityAndTimestamps.push(EntityAndTimestamp.of(entity, currentTimestamp));

        if (this.entityAndTimestamps.size() > LIMIT)
            this.entityAndTimestamps.removeLast();
    }

    @Override
    public void delete(@NonNull EntityId entityId) {
        super.delete(entityId);

        val currentTimestamp = getCurrentTimestamp();
        this.entityIdAndTimestamps.push(EntityIdAndTimestamp.of(entityId, currentTimestamp));

        if (this.entityIdAndTimestamps.size() > LIMIT)
            this.entityIdAndTimestamps.removeLast();
    }

    @Override
    public @NonNull Set<Entity> getSavedEntitiesSince(@NonNull Instant fromTimestamp) {
        return this.entityAndTimestamps.parallelStream()
            .filter(entityAndTimestamp -> entityAndTimestamp.getTimestamp().isAfter(fromTimestamp))
            .map(EntityAndTimestamp::getEntity)
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NonNull Set<EntityId> getDeletedEntityIdsSince(@NonNull Instant fromTimestamp) {
        return this.entityIdAndTimestamps.parallelStream()
            .filter(eat -> eat.getTimestamp().isAfter(fromTimestamp))
            .map(EntityIdAndTimestamp::getEntityId)
            .collect(Collectors.toUnmodifiableSet());
    }

    private static @NonNull Instant getCurrentTimestamp() {
        return Instant.now();
    }

    @Value(staticConstructor = "of")
    private class EntityAndTimestamp {
        @NonNull Entity entity;
        @NonNull Instant timestamp;
    }

    @Value(staticConstructor = "of")
    private class EntityIdAndTimestamp {
        @NonNull EntityId entityId;
        @NonNull Instant timestamp;
    }
}
