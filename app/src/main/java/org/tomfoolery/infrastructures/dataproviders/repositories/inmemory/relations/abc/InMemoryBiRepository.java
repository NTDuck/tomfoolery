package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations.abc;

import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseBiRepository;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.BaseInMemoryRepository;

public abstract class InMemoryBiRepository<BiRelation extends ddd.BiRelation<BiRelationId, FirstEntityId, SecondEntityId>, BiRelationId extends ddd.BiRelationId<FirstEntityId, SecondEntityId>, FirstEntityId extends ddd.EntityId, SecondEntityId extends ddd.EntityId> extends BaseInMemoryRepository<BiRelation, BiRelationId> implements BaseBiRepository<BiRelation, BiRelationId, FirstEntityId, SecondEntityId> {
}
