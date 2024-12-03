package org.tomfoolery.core.dataproviders.aggregates;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseUniRepository;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.List;

public class BaseSynchronizedRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> implements BaseRepository<Entity, EntityId> {
    protected final @NonNull BaseRepository<Entity, EntityId> repository;

    private final @NonNull List<? extends BaseSynchronizedGenerator<Entity, EntityId>> generators;

    private final @NonNull List<? extends BaseUniRepository<?, ?, EntityId>> uniRepositories;
    private final @NonNull BaseBiRepositories<EntityId> biRepositories;

    public static <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> @NonNull BaseSynchronizedRepository<Entity, EntityId> of(@NonNull BaseRepository<Entity, EntityId> repository, @NonNull List<? extends BaseSynchronizedGenerator<Entity, EntityId>> generators, List<? extends BaseUniRepository<?, ?, EntityId>> uniRepositories, @NonNull BaseBiRepositories<EntityId> biRepositories) {
        return new BaseSynchronizedRepository<>(repository, generators, uniRepositories, biRepositories);
    }

    protected BaseSynchronizedRepository(@NonNull BaseRepository<Entity, EntityId> repository, @NonNull List<? extends BaseSynchronizedGenerator<Entity, EntityId>> generators, @NonNull List<? extends BaseUniRepository<?, ?, EntityId>> uniRepositories, @NonNull BaseBiRepositories<EntityId> biRepositories) {
        this.repository = repository;

        this.generators = generators;

        this.uniRepositories = uniRepositories;
        this.biRepositories = biRepositories;
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

        this.uniRepositories.parallelStream()
            .forEach(repository -> repository.synchronizeDeletedEntity(entityId));
        this.biRepositories.synchronizeDeletedEntity(entityId);
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
