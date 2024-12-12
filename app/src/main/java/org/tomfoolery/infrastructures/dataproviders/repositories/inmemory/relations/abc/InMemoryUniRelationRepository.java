package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations.abc;

import lombok.Locked;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseUniRelationRepository;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.BaseInMemoryRepository;

public abstract class InMemoryUniRelationRepository<UniRelation extends ddd.UniRelation<UniRelationId, EntityId>, UniRelationId extends ddd.UniRelationId<EntityId>, EntityId extends ddd.EntityId> extends BaseInMemoryRepository<UniRelation, UniRelationId> implements BaseUniRelationRepository<UniRelation, UniRelationId, EntityId> {
    @Override
    @Locked.Write
    public void synchronizeDeletedEntity(@NonNull EntityId entityId) {
        BaseUniRelationRepository.super.synchronizeDeletedEntity(entityId);
    }
}
