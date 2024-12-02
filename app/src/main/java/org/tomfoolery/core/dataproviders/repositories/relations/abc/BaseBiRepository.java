package org.tomfoolery.core.dataproviders.repositories.relations.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.utils.contracts.ddd;

public interface BaseBiRepository<BiRelation extends ddd.BiRelation<BiRelationId, FirstEntityId, SecondEntityId>, BiRelationId extends ddd.BiRelationId<FirstEntityId, SecondEntityId>, FirstEntityId extends ddd.EntityId, SecondEntityId extends ddd.EntityId> extends BaseRepository<BiRelation, BiRelationId> {
    void synchronizeDeletedFirstEntity(@NonNull FirstEntityId firstEntityId);
    void synchronizeDeletedSecondEntity(@NonNull SecondEntityId secondEntityId);
}
