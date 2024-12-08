package org.tomfoolery.core.dataproviders.generators.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.core.utils.helpers.normalizers.StringNormalizer;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface BaseSearchGenerator<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseSynchronizedGenerator<Entity, EntityId> {
    default @NonNull List<Entity> searchByNormalizedCriterion(@NonNull Function<String, List<Entity>> searchFunction, @NonNull String searchTerm) {
        val normalizedSearchTerm = this.normalize(searchTerm);
        return searchFunction.apply(normalizedSearchTerm);
    }

    default @NonNull String normalize(@NonNull String searchTerm) {
        return StringNormalizer.normalize(searchTerm);
    }

    default @NonNull Collection<String> normalize(@NonNull Collection<String> searchTerms) {
        return searchTerms.parallelStream()
            .map(this::normalize)
            .collect(Collectors.toUnmodifiableList());
    }
}
