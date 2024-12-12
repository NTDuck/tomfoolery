package org.tomfoolery.core.utils.containers.relations;

import lombok.Locked;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseUniRelationRepository;
import org.tomfoolery.core.utils.contracts.ddd;

import java.util.List;

@RequiredArgsConstructor(staticName = "of")
public class UniRelationRepositories<EntityId extends ddd.EntityId> {
    private final @NonNull List<? extends BaseUniRelationRepository<?, ?, EntityId>> uniRelationRepositories;

    @Locked.Write
    public void synchronizeDeletedEntity(@NonNull EntityId entityId) {
        this.uniRelationRepositories.parallelStream()
            .forEach(repository -> repository.synchronizeDeletedEntity(entityId));
    }
}
