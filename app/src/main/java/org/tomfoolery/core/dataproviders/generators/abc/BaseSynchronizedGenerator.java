package org.tomfoolery.core.dataproviders.generators.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseSynchronizableRepository;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BaseSynchronizedGenerator<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseGenerator {
    void synchronizeWithRepository(@NonNull List<Entity> savedEntities, @NonNull List<EntityId> deletedEntityIds);

    default void synchronizeWithRepository(@NonNull BaseSynchronizableRepository<Entity, EntityId> repository, @NonNull Instant sinceTimestamp, @NonNull Instant currentTimestamp) {
        val futureOfSavedEntities = CompletableFuture.supplyAsync(() -> repository.getSavedEntitiesSince(sinceTimestamp));
        val futureOfDeletedEntityIds = CompletableFuture.supplyAsync(() -> repository.getDeletedEntityIdsSince(sinceTimestamp));

        CompletableFuture.allOf(futureOfSavedEntities, futureOfDeletedEntityIds).join();

        val savedEntities = futureOfSavedEntities.join();
        val deletedEntityIds = futureOfDeletedEntityIds.join();

        this.synchronizeWithRepository(savedEntities, deletedEntityIds);

        this.setLastSynchronizedTimestamp(currentTimestamp);
    }

    default void synchronizeWithRepository(@NonNull BaseSynchronizableRepository<Entity, EntityId> repository) {
        val lastSynchronizedTimestamp = this.getLastSynchronizedTimestamp();
        val currentTimestamp = getDefaultCurrentTimestamp();

        this.synchronizeWithRepository(repository, lastSynchronizedTimestamp, currentTimestamp);
    }

    @NonNull Instant getLastSynchronizedTimestamp();
    void setLastSynchronizedTimestamp(@NonNull Instant lastSyncedTimestamp);

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
