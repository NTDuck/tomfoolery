package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.aggregates;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.domain.relations.Review;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.BaseInMemoryRepository;

@NoArgsConstructor(staticName = "of")
public class InMemoryReviewRepository extends BaseInMemoryRepository<Review, Review.Id> implements ReviewRepository {
}
