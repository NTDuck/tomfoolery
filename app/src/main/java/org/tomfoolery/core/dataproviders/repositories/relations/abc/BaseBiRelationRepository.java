package org.tomfoolery.core.dataproviders.repositories.relations.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.utils.contracts.ddd;

public interface BaseBiRelationRepository<BiRelation extends ddd.BiRelation<BiRelationId, FirstEntityId, SecondEntityId>, BiRelationId extends ddd.BiRelationId<FirstEntityId, SecondEntityId>, FirstEntityId extends ddd.EntityId, SecondEntityId extends ddd.EntityId> extends BaseRepository<BiRelation, BiRelationId> {
    default void synchronizeDeletedFirstEntity(@NonNull FirstEntityId entityId) {
        this.showIds().parallelStream()
            .filter(biRelationId -> biRelationId.getFirstEntityId().equals(entityId))
            .forEach(this::delete);
    }

    default void synchronizeDeletedSecondEntity(@NonNull SecondEntityId entityId) {
        this.showIds().parallelStream()
            .filter(biRelationId -> biRelationId.getSecondEntityId().equals(entityId))
            .forEach(this::delete);
    }
}
