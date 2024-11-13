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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BaseInMemorySynchronizableRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseInMemoryRepository<Entity, EntityId> implements BaseSynchronizableRepository<Entity, EntityId> {
    private final @NonNull TimestampedCircularBuffer<Entity> timestampedSavedEntities = TimestampedCircularBuffer.of();
    private final @NonNull TimestampedCircularBuffer<EntityId> timestampedDeletedEntityIds = TimestampedCircularBuffer.of();

    @Override
    public void save(@NonNull Entity entity) {
        super.save(entity);

        this.timestampedSavedEntities.add(entity);
    }

    @Override
    public void delete(@NonNull EntityId entityId) {
        super.delete(entityId);

        this.timestampedDeletedEntityIds.add(entityId);
    }

    @Override
    @Locked
    public @NonNull Set<Entity> getSavedEntitiesSince(@NonNull Instant fromTimestamp) {
        var iteratorOfSavedEntities = timestampedSavedEntities.descendingIterator();
        var iteratorOfDeletedEntityIds = timestampedDeletedEntityIds.descendingIterator();

        var latestSavedEntityEntry = getNextEntry(iteratorOfSavedEntities);
        var latestDeletedEntityIdEntry = getNextEntry(iteratorOfDeletedEntityIds);

        val cachedSavedEntities = CachedEntities.of();
        val cachedDeletedEntityIds = CachedEntityIds.of();

        while (latestSavedEntityEntry != null && latestDeletedEntityIdEntry != null) {
            if (this.isNotWithinFromTimestamp(latestSavedEntityEntry, latestDeletedEntityIdEntry, fromTimestamp))
                break;

            if (latestSavedEntityEntry.getAddedTimestamp().isAfter(latestDeletedEntityIdEntry.getAddedTimestamp())) {
                this.processSavedEntityEntry(latestSavedEntityEntry, cachedSavedEntities, cachedDeletedEntityIds);
                latestSavedEntityEntry = getNextEntry(iteratorOfSavedEntities);
            } else {
                this.processDeletedEntityIdEntry(latestDeletedEntityIdEntry, cachedSavedEntities, cachedDeletedEntityIds);
                latestDeletedEntityIdEntry = getNextEntry(iteratorOfDeletedEntityIds);
            }
        }

        while (latestSavedEntityEntry != null) {
            if (fromTimestamp.isAfter(latestSavedEntityEntry.getAddedTimestamp()))
                break;

            this.processSavedEntityEntry(latestSavedEntityEntry, cachedSavedEntities, cachedDeletedEntityIds);
            latestSavedEntityEntry = getNextEntry(iteratorOfSavedEntities);
        }

        return cachedSavedEntities;
    }

    @Override
    @Locked
    public @NonNull Set<EntityId> getDeletedEntityIdsSince(@NonNull Instant fromTimestamp) {
        var iteratorOfSavedEntities = timestampedSavedEntities.descendingIterator();
        var iteratorOfDeletedEntityIds = timestampedDeletedEntityIds.descendingIterator();

        var latestSavedEntityEntry = getNextEntry(iteratorOfSavedEntities);
        var latestDeletedEntityIdEntry = getNextEntry(iteratorOfDeletedEntityIds);

        val cachedSavedEntities = CachedEntities.of();
        val cachedDeletedEntityIds = CachedEntityIds.of();

        while (latestSavedEntityEntry != null && latestDeletedEntityIdEntry != null) {
            if (this.isNotWithinFromTimestamp(latestSavedEntityEntry, latestDeletedEntityIdEntry, fromTimestamp))
                break;

            if (latestSavedEntityEntry.getAddedTimestamp().isAfter(latestDeletedEntityIdEntry.getAddedTimestamp())) {
                this.processSavedEntityEntry(latestSavedEntityEntry, cachedSavedEntities, cachedDeletedEntityIds);
                latestSavedEntityEntry = getNextEntry(iteratorOfSavedEntities);
            } else {
                this.processDeletedEntityIdEntry(latestDeletedEntityIdEntry, cachedSavedEntities, cachedDeletedEntityIds);
                latestDeletedEntityIdEntry = getNextEntry(iteratorOfDeletedEntityIds);
            }
        }

        while (latestDeletedEntityIdEntry != null) {
            if (fromTimestamp.isAfter(latestDeletedEntityIdEntry.getAddedTimestamp()))
                break;

            this.processDeletedEntityIdEntry(latestDeletedEntityIdEntry, cachedSavedEntities, cachedDeletedEntityIds);
            latestDeletedEntityIdEntry = getNextEntry(iteratorOfDeletedEntityIds);
        }

        return cachedDeletedEntityIds;
    }

    private static <Entry> @Nullable Entry getNextEntry(@NonNull Iterator<Entry> iterator) {
        return iterator.hasNext() ? iterator.next() : null;
    }

    private boolean isNotWithinFromTimestamp(TimestampedCircularBuffer<Entity>.@NonNull Entry savedEntityEntry, TimestampedCircularBuffer<EntityId>.@NonNull Entry deletedEntityIdEntry, @NonNull Instant fromTimestamp) {
        return fromTimestamp.isAfter(savedEntityEntry.getAddedTimestamp())
            || fromTimestamp.isAfter(deletedEntityIdEntry.getAddedTimestamp());
    }

    private void processSavedEntityEntry(TimestampedCircularBuffer<Entity>.@NonNull Entry savedEntityEntry, @NonNull CachedEntities cachedSavedEntities, @NonNull CachedEntityIds cachedDeletedEntityIds) {
        var savedEntity = savedEntityEntry.getElement();

        if (!cachedDeletedEntityIds.contains(savedEntity))
            cachedSavedEntities.add(savedEntity);
    }

    private void processDeletedEntityIdEntry(TimestampedCircularBuffer<EntityId>.@NonNull Entry deletedEntityIdEntry, @NonNull CachedEntities cachedSavedEntities, @NonNull CachedEntityIds cachedDeletedEntityIds) {
        var deletedEntityId = deletedEntityIdEntry.getElement();

        if (!cachedSavedEntities.contains(deletedEntityId))
            cachedDeletedEntityIds.add(deletedEntityId);
    }

    @NoArgsConstructor(staticName = "of")
    private static class TimestampedCircularBuffer<Element> implements Iterable<TimestampedCircularBuffer<Element>.Entry> {
        public static final @Unsigned long BUFFER_LIMIT = (long) Math.pow(2, 22);
        protected final @NonNull Deque<Entry> entries = new ArrayDeque<>();

        void add(@NonNull Element element) {
            val entry = Entry.of(element, Instant.now());

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
            @NonNull Element element;
            @NonNull Instant addedTimestamp;
        }
    }

    @NoArgsConstructor(staticName = "of")
    private class CachedEntities extends HashSet<Entity> {
        public boolean contains(@NonNull EntityId entityId) {
            return this.parallelStream()
                .anyMatch(entity -> entity.getId().equals(entityId));
        }
    }

    @NoArgsConstructor(staticName = "of")
    private class CachedEntityIds extends HashSet<EntityId> {
        public boolean contains(@NonNull Entity entity) {
            return this.parallelStream()
                .anyMatch(entityId -> entity.getId().equals(entityId));
        }
    }
}

/*


 */