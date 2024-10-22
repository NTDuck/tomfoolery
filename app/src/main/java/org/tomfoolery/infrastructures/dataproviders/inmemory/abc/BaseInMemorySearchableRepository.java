package org.tomfoolery.infrastructures.dataproviders.inmemory.abc;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.abc.SearchableRepository;
import org.tomfoolery.core.utils.contracts.ddd.IEntity;
import org.tomfoolery.core.utils.dataclasses.Page;
import org.tomfoolery.core.utils.dataclasses.SearchCriterion;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseInMemorySearchableRepository<Entity extends IEntity<EntityId>, EntityId, AttributeName extends Enum<AttributeName>> extends BaseInMemoryRepository<Entity, EntityId> implements SearchableRepository<Entity, EntityId, AttributeName> {
    protected final @NonNull Map<AttributeName, Function<Entity, ?>> attributeNameToAttributeGetterMap;
    protected final @NonNull Map<SearchCriterion.SearchMethod, BiPredicate<Object, Object>> searchMethodToAttributeAndAttributeValuePredicateMap;

    @Override
    public @NonNull <AttributeValue> Page<Entity> searchPaginatedEntitiesByCriterion(@NonNull SearchCriterion<AttributeName, AttributeValue> searchCriterion, int pageIndex, int pageSize) {
        val pageOffset = Page.getOffset(pageIndex, pageSize);

        val searchMethod = searchCriterion.getSearchMethod();
        val attributeName = searchCriterion.getAttributeName();
        val attributeValue = searchCriterion.getAttributeValue();

        assert(this.attributeNameToAttributeGetterMap.containsKey(attributeName));
        assert(this.searchMethodToAttributeAndAttributeValuePredicateMap.containsKey(searchMethod));

        val paginatedEntities = this.entityIdToEntityMap.values().stream()
            .filter(entity -> {
                val attributeGetter = this.attributeNameToAttributeGetterMap.get(attributeName);
                val attributePredicate = this.searchMethodToAttributeAndAttributeValuePredicateMap.get(searchMethod);

                val attribute = attributeGetter.apply(entity);
                return attributePredicate.test(attribute, attributeValue);
            })
            .skip(pageOffset)
            .limit(pageSize)
            .toList();

        return Page.of(pageIndex, paginatedEntities);
    }
}
