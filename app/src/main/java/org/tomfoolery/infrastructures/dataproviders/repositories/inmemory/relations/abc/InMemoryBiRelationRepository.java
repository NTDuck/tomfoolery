package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations.abc;

import lombok.Locked;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseBiRelationRepository;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.BaseInMemoryRepository;

public abstract class InMemoryBiRelationRepository<BiRelation extends ddd.BiRelation<BiRelationId, FirstEntityId, SecondEntityId>, BiRelationId extends ddd.BiRelationId<FirstEntityId, SecondEntityId>, FirstEntityId extends ddd.EntityId, SecondEntityId extends ddd.EntityId> extends BaseInMemoryRepository<BiRelation, BiRelationId> implements BaseBiRelationRepository<BiRelation, BiRelationId, FirstEntityId, SecondEntityId> {
    @Override
    @Locked.Write
    public void synchronizeDeletedFirstEntity(@NonNull FirstEntityId entityId) {
        BaseBiRelationRepository.super.synchronizeDeletedFirstEntity(entityId);
    }

    @Override
    @Locked.Write
    public void synchronizeDeletedSecondEntity(@NonNull SecondEntityId entityId) {
        BaseBiRelationRepository.super.synchronizeDeletedSecondEntity(entityId);
    }
}
