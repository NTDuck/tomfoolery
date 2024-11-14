package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc;

import lombok.Locked;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseSynchronizableRepository;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BaseInMemorySynchronizableRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseInMemoryRepository<Entity, EntityId> implements BaseSynchronizableRepository<Entity, EntityId> {
    public static final @Unsigned long BUFFER_LIMIT = (long) Math.pow(2, 22);

    private final @NonNull TimestampedCircularBuffer<Entity> timestampedSavedEntities = TimestampedCircularBuffer.of();
    private final @NonNull TimestampedCircularBuffer<Entity> timestampedDeletedEntities = TimestampedCircularBuffer.of();

    @Override
    public void save(@NonNull Entity entity) {
        super.save(entity);

        this.timestampedSavedEntities.add(entity);
    }

    @Override
    public void delete(@NonNull EntityId entityId) {
        val entity = super.entitiesByIds.remove(entityId);

        if (entity != null)
            this.timestampedDeletedEntities.add(entity);
    }

    @Override
    @Locked
    public @NonNull Set<Entity> getSavedEntitiesSince(@NonNull Instant fromTimestamp) {
        return this.getEntitiesSince(fromTimestamp, this.timestampedSavedEntities, this.timestampedDeletedEntities);
    }

    @Override
    @Locked
    public @NonNull Set<Entity> getDeletedEntitiesSince(@NonNull Instant fromTimestamp) {
        return this.getEntitiesSince(fromTimestamp, this.timestampedDeletedEntities, this.timestampedSavedEntities);
    }

    /**
     * Might be generalized for 1 {@code targetEntities} and n {@code referenceEntities}.
     */
    private @NonNull Set<Entity> getEntitiesSince(@NonNull Instant fromTimestamp, @NonNull TimestampedCircularBuffer<Entity> targetEntities, @NonNull TimestampedCircularBuffer<Entity> referenceEntities) {
        Map<EntityId, Entity> cachedTargetEntities = new HashMap<>();
        Map<EntityId, Entity> cachedReferenceEntities = new HashMap<>();

        val iteratorOfTargetEntities = targetEntities.descendingIterator();
        val iteratorOfReferenceEntities = referenceEntities.descendingIterator();

        var entryOfTargetEntities = getNextEntry(iteratorOfTargetEntities);
        var entryOfReferenceEntities = getNextEntry(iteratorOfReferenceEntities);

        while (entryOfTargetEntities != null
            && entryOfReferenceEntities != null) {
            if (!isEntryTimestampValid(entryOfTargetEntities, fromTimestamp))
                break;

            if (!isEntryTimestampValid(entryOfReferenceEntities, fromTimestamp))
                break;

            if (entryOfTargetEntities.getTimestamp().isAfter(entryOfReferenceEntities.getTimestamp())) {
                processEntry(entryOfTargetEntities, cachedTargetEntities, cachedReferenceEntities);
                entryOfTargetEntities = getNextEntry(iteratorOfTargetEntities);
            } else {
                processEntry(entryOfReferenceEntities, cachedReferenceEntities, cachedTargetEntities);
                entryOfReferenceEntities = getNextEntry(iteratorOfReferenceEntities);
            }
        }

        while (entryOfTargetEntities != null) {
            if (!isEntryTimestampValid(entryOfTargetEntities, fromTimestamp))
                break;

            processEntry(entryOfTargetEntities, cachedTargetEntities, cachedReferenceEntities);
            entryOfTargetEntities = getNextEntry(iteratorOfTargetEntities);
        }

        return cachedTargetEntities.values().parallelStream()
            .collect(Collectors.toUnmodifiableSet());
    }

    private static <Entry> @Nullable Entry getNextEntry(@NonNull Iterator<Entry> iterator) {
        return iterator.hasNext() ? iterator.next() : null;
    }

    private static boolean isEntryTimestampValid(TimestampedCircularBuffer<?>.@NonNull Entry entry, @NonNull Instant fromTimestamp) {
        return entry.getTimestamp().isAfter(fromTimestamp);
    }

    private static <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> void processEntry(TimestampedCircularBuffer<Entity>.@NonNull Entry entry, @NonNull Map<EntityId, Entity> targetEntities, @NonNull Map<EntityId, Entity> referenceEntities) {
        val targetEntity = entry.getEntity();
        val targetEntityId = targetEntity.getId();

        if (referenceEntities.containsKey(targetEntityId))
            return;

        targetEntities.putIfAbsent(targetEntityId, targetEntity);
    }

    @NoArgsConstructor(staticName = "of")
    private static class TimestampedCircularBuffer<Entity> implements Iterable<TimestampedCircularBuffer<Entity>.Entry> {
        protected final @NonNull Deque<Entry> entries = new ArrayDeque<>();

        void add(@NonNull Entity entity) {
            val entry = Entry.of(entity, Instant.now());

            this.entries.push(entry);

            if (this.entries.size() > BUFFER_LIMIT)
                this.entries.removeLast();
        }

        @Override
        public @NonNull Iterator<Entry> iterator() {
            return this.entries.iterator();
        }

        public @NonNull Iterator<Entry> descendingIterator() {
            return this.entries.descendingIterator();
        }

        @Value(staticConstructor = "of")
        public class Entry {
            @NonNull Entity entity;
            @NonNull Instant timestamp;
        }
    }
}
