package org.tomfoolery.core.dataproviders.repositories.aggregates.abc;

import lombok.Locked;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.Set;

public class BaseAggregateRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> implements BaseRepository<Entity, EntityId> {
    protected final @NonNull BaseRepository<Entity, EntityId> repository;

    protected BaseAggregateRepository(@NonNull BaseRepository<Entity, EntityId> repository) {
        this.repository = repository;
    }

    @Override
    @Locked.Write
    public void save(@NonNull Entity entity) {
        this.repository.save(entity);
    }

    @Override
    @Locked.Write
    public void delete(@NonNull EntityId entityId) {
        this.repository.delete(entityId);
    }

    @Override
    @Locked.Read
    public @Nullable Entity getById(@NonNull EntityId entityId) {
        return this.repository.getById(entityId);
    }

    @Override
    @Locked.Read
    public @NonNull Set<EntityId> showIds() {
        return this.repository.showIds();
    }

    @Override
    @Locked.Read
    public @Nullable Page<EntityId> showIdsPage(@Unsigned int pageIndex, @Unsigned int maxPageSize) {
        return this.repository.showIdsPage(pageIndex, maxPageSize);
    }

    @Override
    @Locked.Read
    public @NonNull Set<Entity> show() {
        return this.repository.show();
    }

    @Override
    @Locked.Read
    public @Nullable Page<Entity> showPage(@Unsigned int pageIndex, @Unsigned int maxPageSize) {
        return this.repository.showPage(pageIndex, maxPageSize);
    }

    @Override
    @Locked.Read
    public boolean contains(@NonNull EntityId entityId) {
        return this.repository.contains(entityId);
    }

    @Override
    @Locked.Read
    public @Unsigned int size() {
        return this.repository.size();
    }
}
