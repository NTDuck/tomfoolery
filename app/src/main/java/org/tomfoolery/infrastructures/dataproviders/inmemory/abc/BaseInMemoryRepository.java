package org.tomfoolery.infrastructures.dataproviders.inmemory.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.abc.BaseRepository;
import org.tomfoolery.core.utils.contracts.ddd.IEntity;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.HashMap;
import java.util.Map;

public class BaseInMemoryRepository<Entity extends IEntity<EntityId>, EntityId> implements BaseRepository<Entity, EntityId> {
    protected final @NonNull Map<EntityId, Entity> entityIdToEntityMap = new HashMap<>();

    @Override
    public boolean contains(@NonNull EntityId entityId) {
        return this.entityIdToEntityMap.containsKey(entityId);
    }

    @Override
    public @NonNull Page<Entity> showPaginatedEntities(int pageIndex, int pageSize) {
        val pageOffset = Page.getOffset(pageIndex, pageSize);

        val paginatedEntities = this.entityIdToEntityMap.values().stream()
            .skip(pageOffset)
            .limit(pageSize)
            .toList();

        return Page.of(pageIndex, paginatedEntities);
    }

    @Override
    public @Nullable Entity getById(@NonNull EntityId entityId) {
        return entityIdToEntityMap.get(entityId);
    }

    @Override
    public void delete(@NonNull EntityId entityId) {
        this.entityIdToEntityMap.remove(entityId);
    }

    @Override
    public void save(@NonNull Entity entity) {
        val entityId = entity.getId();
        this.entityIdToEntityMap.put(entityId, entity);
    }
}
