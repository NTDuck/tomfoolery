package org.tomfoolery.core.dataproviders.generators.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.contracts.ddd;

public interface BaseSynchronizedGenerator<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseGenerator {
    void synchronizeSavedEntity(@NonNull Entity savedEntity);
    void synchronizeDeletedEntity(@NonNull Entity deletedEntity);
}
