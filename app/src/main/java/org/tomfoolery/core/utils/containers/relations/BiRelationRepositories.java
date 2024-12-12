package org.tomfoolery.core.utils.containers.relations;

import lombok.Locked;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseBiRelationRepository;
import org.tomfoolery.core.utils.contracts.ddd;

import java.util.List;

@RequiredArgsConstructor(staticName = "of")
public class BiRelationRepositories<EntityId extends ddd.EntityId> {
    private final @NonNull List<? extends BaseBiRelationRepository<?, ?, EntityId, ?>> firstBiRelationRepositories;
    private final @NonNull List<? extends BaseBiRelationRepository<?, ?, ?, EntityId>> secondBiRelationRepositories;

    @Locked.Write
    public void synchronizeDeletedEntity(@NonNull EntityId entityId) {
        this.firstBiRelationRepositories.parallelStream()
            .forEach(repository -> repository.synchronizeDeletedFirstEntity(entityId));

        this.secondBiRelationRepositories.parallelStream()
            .forEach(repository -> repository.synchronizeDeletedSecondEntity(entityId));
    }
}
