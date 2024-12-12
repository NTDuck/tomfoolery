package org.tomfoolery.core.dataproviders.repositories.aggregates.synchronizeds.abc;

import lombok.Locked;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.dataproviders.repositories.aggregates.abc.BaseAggregateRepository;
import org.tomfoolery.core.utils.containers.relations.BiRelationRepositories;
import org.tomfoolery.core.utils.containers.relations.UniRelationRepositories;
import org.tomfoolery.core.utils.contracts.ddd;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BaseSynchronizedRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseAggregateRepository<Entity, EntityId> {
    private final @NonNull List<? extends BaseSynchronizedGenerator<Entity, EntityId>> generators;

    private final @NonNull UniRelationRepositories<EntityId> uniRelationRepositories;
    private final @NonNull BiRelationRepositories<EntityId> biRelationRepositories;

    public static <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> @NonNull BaseSynchronizedRepository<Entity, EntityId> of(@NonNull BaseRepository<Entity, EntityId> repository, @NonNull List<? extends BaseSynchronizedGenerator<Entity, EntityId>> generators, UniRelationRepositories<EntityId> uniRelationRepositories, @NonNull BiRelationRepositories<EntityId> biRelationRepositories) {
        return new BaseSynchronizedRepository<>(repository, generators, uniRelationRepositories, biRelationRepositories);
    }

    protected BaseSynchronizedRepository(@NonNull BaseRepository<Entity, EntityId> repository, @NonNull List<? extends BaseSynchronizedGenerator<Entity, EntityId>> generators, @NonNull UniRelationRepositories<EntityId> uniRelationRepositories, @NonNull BiRelationRepositories<EntityId> biRelationRepositories) {
        super(repository);

        this.generators = generators;

        this.uniRelationRepositories = uniRelationRepositories;
        this.biRelationRepositories = biRelationRepositories;
    }

    @Override
    @Locked.Write
    public void save(@NonNull Entity entity) {
        this.repository.save(entity);

        this.generators.parallelStream()
            .forEach(generator -> generator.synchronizeSavedEntity(entity));
    }

    @Override
    @Locked.Write
    public void delete(@NonNull EntityId entityId) {
        val entity = this.repository.getById(entityId);

        if (entity == null)
            return;

        CompletableFuture.allOf(
            CompletableFuture.runAsync(() -> this.repository.delete(entityId)),
            CompletableFuture.runAsync(() -> this.generators.parallelStream()
                .forEach(generator -> generator.synchronizeDeletedEntity(entity))),
            CompletableFuture.runAsync(() -> this.uniRelationRepositories.synchronizeDeletedEntity(entityId)),
            CompletableFuture.runAsync(() -> this.biRelationRepositories.synchronizeDeletedEntity(entityId))
        ).join();
    }
}
