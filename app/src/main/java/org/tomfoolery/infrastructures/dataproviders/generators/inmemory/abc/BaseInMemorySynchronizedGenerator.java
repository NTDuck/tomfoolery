package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.abc;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.time.Instant;

@Getter @Setter
public abstract class BaseInMemorySynchronizedGenerator<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> implements BaseSynchronizedGenerator<Entity, EntityId> {
    private @NonNull Instant lastSynchronizedTimestamp = Instant.EPOCH;
}
