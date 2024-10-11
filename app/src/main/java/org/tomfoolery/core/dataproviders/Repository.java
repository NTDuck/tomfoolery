package org.tomfoolery.core.dataproviders;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;

public interface Repository<Entity, EntityId> {
    void save(@NonNull Entity entity);
    void delete(@NonNull EntityId entityId);

    @Nullable Entity getById(EntityId entityId);
    @NonNull Collection<Entity> show();

    default boolean contains(@NonNull EntityId entityId) {
        return this.getById(entityId) != null;
    }
}
