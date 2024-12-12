package org.tomfoolery.core.dataproviders.repositories.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface BaseRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> {
    void save(@NonNull Entity entity);
    void delete(@NonNull EntityId entityId);

    @Nullable Entity getById(@NonNull EntityId entityId);
    @NonNull List<Entity> show();

    /**
     * Sacrifices order for performance.
     */
    default @Nullable Page<Entity> showPage(@Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val entityIdsPage = this.showIdsPage(pageIndex, maxPageSize);

        if (entityIdsPage == null)
            return null;

        return entityIdsPage.map(this::getById);
    }

    default @NonNull Set<EntityId> showIds() {
        return this.show()
            .parallelStream()
            .map(Entity::getId)
            .collect(Collectors.toUnmodifiableSet());
    }

    default @Nullable Page<EntityId> showIdsPage(@Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedEntityIds = this.showIds().parallelStream()
            .collect(Collectors.toUnmodifiableList());
        return Page.fromUnpaginated(unpaginatedEntityIds, pageIndex, maxPageSize);
    }

    default boolean contains(@NonNull EntityId entityId) {
        return this.getById(entityId) != null;
    }

    default @Unsigned int size() {
        return (int) this.show().parallelStream().count();
    }
}
