package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.domain.relations.Review;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.BaseInMemoryRepository;
import org.tomfoolery.infrastructures.utils.helpers.comparators.DocumentComparator;
import org.tomfoolery.infrastructures.utils.helpers.comparators.UserComparator;

import java.util.Comparator;

@NoArgsConstructor(staticName = "of")
public class InMemoryReviewRepository extends BaseInMemoryRepository<Review, Review.Id> implements ReviewRepository {
    @Override
    protected @NonNull Comparator<Review.Id> getEntityIdComparator() {
        return Comparator.comparing(Review.Id::getFirstEntityId, DocumentComparator.compareId())
            .thenComparing(Review.Id::getSecondEntityId, UserComparator.compareId());
    }
}
