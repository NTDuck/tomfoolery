package org.tomfoolery.core.dataproviders.repositories.relations.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.utils.contracts.ddd;

public interface BaseBiRepository<BiRelation extends ddd.BiRelation<BiRelationId, FirstEntityId, SecondEntityId>, BiRelationId extends ddd.BiRelationId<FirstEntityId, SecondEntityId>, FirstEntityId extends ddd.EntityId, SecondEntityId extends ddd.EntityId> extends BaseRepository<BiRelation, BiRelationId> {
    default void synchronizeDeletedFirstEntity(@NonNull FirstEntityId entityId) {
        this.show().parallelStream()
            .filter(biRelation -> biRelation.getId().getFirstEntityId().equals(entityId))
            .map(BiRelation::getId)
            .forEach(this::delete);
    }

    default void synchronizeDeletedSecondEntity(@NonNull SecondEntityId entityId) {
        this.show().parallelStream()
            .filter(biRelation -> biRelation.getId().getSecondEntityId().equals(entityId))
            .map(BiRelation::getId)
            .forEach(this::delete);
    }
}
