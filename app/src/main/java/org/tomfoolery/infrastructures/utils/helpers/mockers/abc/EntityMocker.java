package org.tomfoolery.infrastructures.utils.helpers.mockers.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.contracts.ddd;

public interface EntityMocker<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> {
    @NonNull EntityId createMockEntityId();
    @NonNull Entity createMockEntityWithId(@NonNull EntityId entityId);

    default @NonNull Entity createMockEntity() {
        return this.createMockEntityWithId(this.createMockEntityId());
    }
}
