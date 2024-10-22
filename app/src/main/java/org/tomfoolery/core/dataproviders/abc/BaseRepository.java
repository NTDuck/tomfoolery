package org.tomfoolery.core.dataproviders.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.utils.contracts.ddd.IEntity;
import org.tomfoolery.core.utils.dataclasses.Page;

public interface BaseRepository<Entity extends IEntity<EntityId>, EntityId> {
    void save(@NonNull Entity entity);
    void delete(@NonNull EntityId entityId);

    @Nullable Entity getById(@NonNull EntityId entityId);
    @NonNull Page<Entity> showPaginatedEntities(int pageIndex, int pageSize);

    default boolean contains(@NonNull EntityId entityId) {
        return this.getById(entityId) != null;
    }
}
