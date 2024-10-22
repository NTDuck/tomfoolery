package org.tomfoolery.core.dataproviders.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.contracts.ddd.IEntity;
import org.tomfoolery.core.utils.dataclasses.Page;
import org.tomfoolery.core.utils.dataclasses.SearchCriterion;

public interface SearchableRepository<Entity extends IEntity<EntityId>, EntityId, AttributeName extends Enum<AttributeName>> extends BaseRepository<Entity, EntityId> {
    <AttributeValue> @NonNull Page<Entity> searchPaginatedEntitiesByCriterion(@NonNull SearchCriterion<AttributeName, AttributeValue> searchCriterion, int pageIndex, int pageSize);
}
