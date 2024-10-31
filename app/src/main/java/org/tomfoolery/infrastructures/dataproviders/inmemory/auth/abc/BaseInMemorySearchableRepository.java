package org.tomfoolery.infrastructures.dataproviders.inmemory.auth.abc;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseInMemorySearchableRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId, AttributeName extends Enum<AttributeName>> extends BaseInMemoryRepository<Entity, EntityId> implements SearchableRepository<Entity, EntityId, AttributeName> {
    private final @NonNull Map<AttributeName, Function<Entity, ?>> attributeNameToAttributeGetterMap;
    private final @NonNull Map<SearchCriterion.SearchMethod, BiPredicate<Object, Object>> searchMethodToAttributeAndAttributeValuePredicateMap;

    @Override
    public @NonNull <AttributeValue> Collection<Entity> searchByCriterion(@NonNull SearchCriterion<AttributeName, AttributeValue> searchCriterion) {
        val searchMethod = searchCriterion.getSearchMethod();
        val attributeName = searchCriterion.getAttributeName();
        val attributeValue = searchCriterion.getAttributeValue();

        assert(this.attributeNameToAttributeGetterMap.containsKey(attributeName));
        assert(this.searchMethodToAttributeAndAttributeValuePredicateMap.containsKey(searchMethod));

        return this.show().stream()
            .filter(entity -> {
                val attributeGetter = this.attributeNameToAttributeGetterMap.get(attributeName);
                val attributePredicate = this.searchMethodToAttributeAndAttributeValuePredicateMap.get(searchMethod);

                val attribute = attributeGetter.apply(entity);
                return attributePredicate.test(attribute, attributeValue);
            })
            .collect(Collectors.toUnmodifiableList());
    }
}
