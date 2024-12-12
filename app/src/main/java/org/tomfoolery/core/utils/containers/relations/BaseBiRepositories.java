package org.tomfoolery.core.dataproviders.repositories.aggregates;

import lombok.Locked;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseBiRepository;
import org.tomfoolery.core.utils.contracts.ddd;

import java.util.List;

public class BaseBiRepositories<EntityId extends ddd.EntityId> {
    private final @NonNull List<? extends BaseBiRepository<?, ?, EntityId, ?>> firstBiRepositories;
    private final @NonNull List<? extends BaseBiRepository<?, ?, ?, EntityId>> secondBiRepositories;

    public static <EntityId extends ddd.EntityId> @NonNull BaseBiRepositories<EntityId> of(@NonNull List<? extends BaseBiRepository<?, ?, EntityId, ?>> firstBiRepositories, @NonNull List<? extends BaseBiRepository<?, ?, ?, EntityId>> secondBiRepositories) {
        return new BaseBiRepositories<>(firstBiRepositories, secondBiRepositories);
    }

    protected BaseBiRepositories(@NonNull List<? extends BaseBiRepository<?, ?, EntityId, ?>> firstBiRepositories, @NonNull List<? extends BaseBiRepository<?, ?, ?, EntityId>> secondBiRepositories) {
        this.firstBiRepositories = firstBiRepositories;
        this.secondBiRepositories = secondBiRepositories;
    }

    @Locked.Write
    public void synchronizeDeletedEntity(@NonNull EntityId entityId) {
        this.firstBiRepositories.parallelStream()
            .forEach(repository -> repository.synchronizeDeletedFirstEntity(entityId));

        this.secondBiRepositories.parallelStream()
            .forEach(repository -> repository.synchronizeDeletedSecondEntity(entityId));
    }
}
