package org.tomfoolery.core.dataproviders.repositories.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.utils.contracts.ddd.ddd;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.List;

public interface BaseRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> {
    void save(@NonNull Entity entity);
    void delete(@NonNull EntityId entityId);

    @Nullable Entity getById(@NonNull EntityId entityId);
    @NonNull List<Entity> show();

    default boolean contains(@NonNull EntityId entityId) {
        return this.getById(entityId) != null;
    }

    default @Nullable Page<Entity> showPaginated(@Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedEntities = this.show();
        return Page.fromUnpaginated(unpaginatedEntities, pageIndex, maxPageSize);
    }
}
