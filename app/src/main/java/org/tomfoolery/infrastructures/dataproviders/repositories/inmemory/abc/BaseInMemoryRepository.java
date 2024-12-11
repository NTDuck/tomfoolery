package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc;

import lombok.Locked;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.utils.contracts.ddd;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public abstract class BaseInMemoryRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> implements BaseRepository<Entity, EntityId> {
    protected final @NonNull Map<EntityId, Entity> entitiesByIds;

    protected BaseInMemoryRepository() {
        val entityComparator = this.getEntityIdComparator();
        this.entitiesByIds = Collections.synchronizedNavigableMap(new TreeMap<>(entityComparator));
    }

    protected abstract @NonNull Comparator<EntityId> getEntityIdComparator();

    @Override
    @Locked.Write
    public void save(@NonNull Entity entity) {
        val entityId = entity.getId();
        this.entitiesByIds.put(entityId, entity);
    }

    @Override
    @Locked.Write
    public void delete(@NonNull EntityId entityId) {
        this.entitiesByIds.remove(entityId);
    }

    @Override
    @Locked.Read
    public @Nullable Entity getById(@NonNull EntityId entityId) {
        return this.entitiesByIds.get(entityId);
    }

    @Override
    @Locked.Read
    public @NonNull List<Entity> show() {
        return this.entitiesByIds.values()
            .parallelStream()
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Locked.Read
    public boolean contains(@NonNull EntityId entityId) {
        return this.entitiesByIds.containsKey(entityId);
    }

    @Override
    @Locked.Read
    public @Unsigned int size() {
        return this.entitiesByIds.size();
    }
}
