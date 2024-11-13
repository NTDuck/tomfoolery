package org.tomfoolery.core.dataproviders.generators.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseSynchronizableRepository;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface BaseSynchronizedGenerator<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseGenerator {
    void synchronizeRecentlySavedEntities(@NonNull Set<Entity> savedEntities);
    void synchronizeRecentlyDeletedEntityIds(@NonNull Set<EntityId> deletedEntityIds);

    @NonNull Instant getLastSynchronizedTimestamp();
    void setLastSynchronizedTimestamp(@NonNull Instant lastSynchronizedTimestamp);

    default void synchronizeWithRepository(@NonNull BaseSynchronizableRepository<Entity, EntityId> repository, @NonNull Instant lastSynchronizedTimestamp, @NonNull Instant currentTimestamp) {
        val futureOfCreationSynchronization = CompletableFuture.runAsync(() -> {
            val savedEntities = repository.getSavedEntitiesSince(lastSynchronizedTimestamp);
            this.synchronizeRecentlySavedEntities(savedEntities);
        });
        val futureOfDeletionSynchronization = CompletableFuture.runAsync(() -> {
            val deletedEntityIds = repository.getDeletedEntityIdsSince(lastSynchronizedTimestamp);
            this.synchronizeRecentlyDeletedEntityIds(deletedEntityIds);
        });

        CompletableFuture.allOf(futureOfCreationSynchronization, futureOfDeletionSynchronization).join();

        this.setLastSynchronizedTimestamp(currentTimestamp);
    }

    default void synchronizeWithRepository(@NonNull BaseSynchronizableRepository<Entity, EntityId> repository) {
        val lastSynchronizedTimestamp = this.getLastSynchronizedTimestamp();
        val currentTimestamp = getDefaultCurrentTimestamp();

        this.synchronizeWithRepository(repository, lastSynchronizedTimestamp, currentTimestamp);
    }

    default boolean isSynchronizedIntervalElapsed(@NonNull Duration synchronizationInterval, @NonNull Instant currentTimestamp) {
        val lastSynchronizedTimestamp = this.getLastSynchronizedTimestamp();

        return Duration.between(lastSynchronizedTimestamp, currentTimestamp)
            .compareTo(synchronizationInterval) > 0;
    }

    default boolean isSynchronizedIntervalElapsed(@NonNull Duration synchronizationInterval) {
        val currentTimestamp = getDefaultCurrentTimestamp();
        return this.isSynchronizedIntervalElapsed(synchronizationInterval, currentTimestamp);
    }

    private static @NonNull Instant getDefaultCurrentTimestamp() {
        return Instant.now();
    }
}
