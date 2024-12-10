package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations.abc;

import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseUniRepository;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.BaseInMemoryRepository;

public class InMemoryUniRepository<UniRelation extends ddd.UniRelation<UniRelationId, EntityId>, UniRelationId extends ddd.UniRelationId<EntityId>, EntityId extends ddd.EntityId> extends BaseInMemoryRepository<UniRelation, UniRelationId> implements BaseUniRepository<UniRelation, UniRelationId, EntityId> {
}
