package org.tomfoolery.core.dataproviders.repositories.relations.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.utils.contracts.ddd;

public interface BaseUniRelationRepository<UniRelation extends ddd.UniRelation<UniRelationId, EntityId>, UniRelationId extends ddd.UniRelationId<EntityId>, EntityId extends ddd.EntityId> extends BaseRepository<UniRelation, UniRelationId> {
    default void synchronizeDeletedEntity(@NonNull EntityId entityId) {
        this.showIds().parallelStream()
            .filter(uniRelationId -> uniRelationId.getEntityId().equals(entityId))
            .forEach(this::delete);
    }
}
