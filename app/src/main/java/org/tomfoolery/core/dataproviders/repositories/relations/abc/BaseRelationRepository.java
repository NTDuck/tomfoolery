package org.tomfoolery.core.dataproviders.repositories.relations.abc;

import org.tomfoolery.core.utils.contracts.ddd.ddd;

public interface BaseRelationRepository<Relation extends ddd.Relation<FirstEntityId, SecondEntityId>, FirstEntityId extends ddd.EntityId, SecondEntityId extends ddd.EntityId> {
}
