package org.tomfoolery.core.dataproviders.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.core.utils.dataclasses.Page;
import org.tomfoolery.core.utils.dataclasses.SearchCriterion;

import java.util.Collection;

public interface SearchableRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId, AttributeName extends Enum<AttributeName>> extends BaseRepository<Entity, EntityId> {
    <AttributeValue> @NonNull Collection<Entity> searchByCriterion(@NonNull SearchCriterion<AttributeName, AttributeValue> searchCriterion);

    default <AttributeValue> @Nullable Page<Entity> searchPaginatedByCriterion(@NonNull SearchCriterion<AttributeName, AttributeValue> searchCriterion, int pageIndex, int maxPageSize) {
        val unpaginatedEntities = this.searchByCriterion(searchCriterion);
        return Page.of(unpaginatedEntities, pageIndex, maxPageSize);
    }
}
