package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.utils.contracts.ddd;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BaseInMemoryRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> implements BaseRepository<Entity, EntityId> {
    protected final @NonNull Map<EntityId, Entity> entitiesByIds = new ConcurrentHashMap<>();

    @Override
    public void save(@NonNull Entity entity) {
        val entityId = entity.getId();
        this.entitiesByIds.put(entityId, entity);
    }

    @Override
    public void delete(@NonNull EntityId entityId) {
        this.entitiesByIds.remove(entityId);
    }

    @Override
    public @Nullable Entity getById(@NonNull EntityId entityId) {
        return this.entitiesByIds.get(entityId);
    }

    @Override
    public @NonNull List<Entity> show() {
        return this.entitiesByIds.values()
            .parallelStream()
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean contains(@NonNull EntityId entityId) {
        return this.entitiesByIds.containsKey(entityId);
    }
}
