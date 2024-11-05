package org.tomfoolery.core.dataproviders.repositories.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.utils.contracts.ddd.ddd;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.Collection;

public interface BaseRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends ddd.Repository<Entity, EntityId> {
    void save(@NonNull Entity entity);
    void delete(@NonNull EntityId entityId);

    @Nullable Entity getById(@NonNull EntityId entityId);
    @NonNull Collection<Entity> show();

    default boolean contains(@NonNull EntityId entityId) {
        return this.getById(entityId) != null;
    }

    default @Nullable Page<Entity> showPaginated(int pageIndex, int maxPageSize) {
        val unpaginatedEntities = this.show();
        return Page.of(unpaginatedEntities, pageIndex, maxPageSize);
    }
}
