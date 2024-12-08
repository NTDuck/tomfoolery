package org.tomfoolery.core.dataproviders.repositories.relations.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.utils.contracts.ddd;

public interface BaseUniRepository<UniRelation extends ddd.UniRelation<UniRelationId, EntityId>, UniRelationId extends ddd.UniRelationId<EntityId>, EntityId extends ddd.EntityId> extends BaseRepository<UniRelation, UniRelationId> {
    default void synchronizeDeletedEntity(@NonNull EntityId entityId) {
        this.show().parallelStream()
            .filter(unirelation -> unirelation.getId().getEntityId().equals(entityId))
            .map(ddd.Entity::getId)
            .forEach(this::delete);
    }
}
