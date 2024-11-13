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
    private final @NonNull TimestampedCircularBuffer timestampedSavedEntities = TimestampedCircularBuffer.of();
    private final @NonNull TimestampedCircularBuffer timestampedDeletedEntityIds = TimestampedCircularBuffer.of();

    @Override
    public void save(@NonNull Entity entity) {
        super.save(entity);

        this.timestampedSavedEntities.add(entity);
    }

    @Override
    public void delete(@NonNull EntityId entityId) {
        val entity = super.entitiesByIds.remove(entityId);

        if (entity != null)
            this.timestampedDeletedEntityIds.add(entity);
    }

    @Override
    @Locked
    public @NonNull Set<Entity> getSavedEntitiesSince(@NonNull Instant fromTimestamp) {
        Map<EntityId, Entity> cachedSavedEntities = new HashMap<>();
        Map<EntityId, Entity> cachedDeletedEntityIds = new HashMap<>();

        val iteratorOfSavedEntities = timestampedSavedEntities.descendingIterator();
        val iteratorOfDeletedEntityIds = timestampedDeletedEntityIds.descendingIterator();

        var latestCreationEntry = getNextEntry(iteratorOfSavedEntities);
        var latestDeletionEntry = getNextEntry(iteratorOfDeletedEntityIds);

        while (latestCreationEntry != null && latestDeletionEntry != null) {
            val latestCreationTimestamp = latestCreationEntry.getTimestamp();
            val latestDeletionTimestamp = latestDeletionEntry.getTimestamp();

            if (fromTimestamp.isAfter(latestCreationTimestamp)
                || fromTimestamp.isAfter(latestDeletionTimestamp))
                break;

            if (latestCreationTimestamp.isAfter(latestDeletionTimestamp)) {
                val savedEntity = latestCreationEntry.getEntity();
                val savedEntityId = savedEntity.getId();

                if (!cachedDeletedEntityIds.containsKey(savedEntityId))
                    cachedSavedEntities.putIfAbsent(savedEntityId, savedEntity);

                latestCreationEntry = getNextEntry(iteratorOfSavedEntities);

            } else {
                val deletedEntity = latestDeletionEntry.getEntity();
                val deletedEntityId = deletedEntity.getId();

                if (!cachedSavedEntities.containsKey(deletedEntityId))
                    cachedDeletedEntityIds.putIfAbsent(deletedEntityId, deletedEntity);

                latestDeletionEntry = getNextEntry(iteratorOfDeletedEntityIds);
            }
        }

        while (latestCreationEntry != null) {
            val latestCreationTimestamp = latestCreationEntry.getTimestamp();

            if (fromTimestamp.isAfter(latestCreationTimestamp))
                break;

            val savedEntity = latestCreationEntry.getEntity();
            val savedEntityId = savedEntity.getId();

            if (!cachedDeletedEntityIds.containsKey(savedEntityId))
                cachedSavedEntities.put(savedEntityId, savedEntity);

            latestCreationEntry = getNextEntry(iteratorOfSavedEntities);
        }

        return cachedSavedEntities.values().parallelStream()
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @Locked
    public @NonNull Set<Entity> getDeletedEntitiesSince(@NonNull Instant fromTimestamp) {
        Map<EntityId, Entity> cachedSavedEntities = new HashMap<>();
        Map<EntityId, Entity> cachedDeletedEntities = new HashMap<>();

        val iteratorOfSavedEntities = timestampedSavedEntities.descendingIterator();
        val iteratorOfDeletedEntityIds = timestampedDeletedEntityIds.descendingIterator();

        var latestCreationEntry = getNextEntry(iteratorOfSavedEntities);
        var latestDeletionEntry = getNextEntry(iteratorOfDeletedEntityIds);

        while (latestCreationEntry != null && latestDeletionEntry != null) {
            val latestCreationTimestamp = latestCreationEntry.getTimestamp();
            val latestDeletionTimestamp = latestDeletionEntry.getTimestamp();

            if (fromTimestamp.isAfter(latestCreationTimestamp)
                || fromTimestamp.isAfter(latestDeletionTimestamp))
                break;

            if (latestCreationTimestamp.isAfter(latestDeletionTimestamp)) {
                val savedEntity = latestCreationEntry.getEntity();
                val savedEntityId = savedEntity.getId();

                if (!cachedDeletedEntities.containsKey(savedEntityId))
                    cachedSavedEntities.putIfAbsent(savedEntityId, savedEntity);

                latestCreationEntry = getNextEntry(iteratorOfSavedEntities);

            } else {
                val deletedEntity = latestDeletionEntry.getEntity();
                val deletedEntityId = deletedEntity.getId();

                if (!cachedSavedEntities.containsKey(deletedEntityId))
                    cachedDeletedEntities.putIfAbsent(deletedEntityId, deletedEntity);

                latestDeletionEntry = getNextEntry(iteratorOfDeletedEntityIds);
            }
        }

        while (latestDeletionEntry != null) {
            val latestDeletionTimestamp = latestDeletionEntry.getTimestamp();

            if (fromTimestamp.isAfter(latestDeletionTimestamp))
                break;

            val deletedEntity = latestDeletionEntry.getEntity();
            val deletedEntityId = deletedEntity.getId();

            if (!cachedSavedEntities.containsKey(deletedEntityId))
                cachedDeletedEntities.putIfAbsent(deletedEntityId, deletedEntity);

            latestDeletionEntry = getNextEntry(iteratorOfDeletedEntityIds);
        }

        return cachedDeletedEntities.values().parallelStream()
            .collect(Collectors.toUnmodifiableSet());
    }

    private static <Entry> @Nullable Entry getNextEntry(@NonNull Iterator<Entry> iterator) {
        return iterator.hasNext() ? iterator.next() : null;
    }

    @NoArgsConstructor(staticName = "of")
    private class TimestampedCircularBuffer implements Iterable<TimestampedCircularBuffer.Entry> {
        public static final @Unsigned long BUFFER_LIMIT = (long) Math.pow(2, 22);
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
        protected class Entry {
            @NonNull Entity entity;
            @NonNull Instant timestamp;
        }
    }
}
