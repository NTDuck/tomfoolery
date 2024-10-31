package org.tomfoolery.infrastructures.dataproviders.inmemory.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.abc.BaseRepository;
import org.tomfoolery.core.utils.contracts.ddd;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BaseInMemoryRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> implements BaseRepository<Entity, EntityId> {
    protected final @NonNull Map<EntityId, Entity> entityIdToEntityMap = new HashMap<>();

    @Override
    public void save(@NonNull Entity entity) {
        val entityId = entity.getId();
        this.entityIdToEntityMap.put(entityId, entity);
    }

    @Override
    public void delete(@NonNull EntityId entityId) {
        this.entityIdToEntityMap.remove(entityId);
    }

    @Override
    public @Nullable Entity getById(@NonNull EntityId entityId) {
        return this.entityIdToEntityMap.get(entityId);
    }

    @Override
    public @NonNull Collection<Entity> show() {
        return this.entityIdToEntityMap.values();
    }

    @Override
    public boolean contains(@NonNull EntityId entityId) {
        return this.entityIdToEntityMap.containsKey(entityId);
    }
}
