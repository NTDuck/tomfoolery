package org.tomfoolery.core.dataproviders.aggregates;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.utils.contracts.ddd.ddd;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.List;

public class BaseSynchronizedRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> implements BaseRepository<Entity, EntityId> {
    private final @NonNull BaseRepository<Entity, EntityId> repository;
    private final @NonNull List<BaseSynchronizedGenerator<Entity, EntityId>> generators;

    public static <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> @NonNull BaseSynchronizedRepository<Entity, EntityId> of(@NonNull BaseRepository<Entity, EntityId> repository, @NonNull List<BaseSynchronizedGenerator<Entity, EntityId>> generators) {
        return new BaseSynchronizedRepository<>(repository, generators);
    }

    protected BaseSynchronizedRepository(@NonNull BaseRepository<Entity, EntityId> repository, @NonNull List<BaseSynchronizedGenerator<Entity, EntityId>> generators) {
        this.repository = repository;
        this.generators = generators;
    }

    @Override
    public void save(@NonNull Entity entity) {
        this.repository.save(entity);

        this.generators.parallelStream()
            .forEach(generator -> generator.synchronizeSavedEntity(entity));
    }

    @Override
    public void delete(@NonNull EntityId entityId) {
        val entity = this.repository.getById(entityId);

        if (entity == null)
            return;

        this.repository.delete(entityId);

        this.generators.parallelStream()
            .forEach(generator -> generator.synchronizeDeletedEntity(entity));
    }

    @Override
    public @Nullable Entity getById(@NonNull EntityId entityId) {
        return this.repository.getById(entityId);
    }

    @Override
    public @NonNull List<Entity> show() {
        return this.repository.show();
    }

    @Override
    public boolean contains(@NonNull EntityId entityId) {
        return this.repository.contains(entityId);
    }

    @Override
    public @Nullable Page<Entity> showPaginated(@Unsigned int pageIndex, @Unsigned int maxPageSize) {
        return this.repository.showPaginated(pageIndex, maxPageSize);
    }
}
