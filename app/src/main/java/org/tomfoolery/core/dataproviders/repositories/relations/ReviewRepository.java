package org.tomfoolery.core.dataproviders.repositories.relations;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseBiRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.Review;
import org.tomfoolery.core.domain.users.Patron;

public interface ReviewRepository extends BaseBiRepository<Review, Review.Id, Document.Id, Patron.Id> {
    default @Unsigned double calculateAverageRating(Document.@NonNull Id documentId) {
        return this.show().parallelStream()
            .filter(review -> review.getId().getFirstEntityId().equals(documentId))
            .mapToDouble(Review::getRating)
            .average().
            orElse(0);
    }

    default @Unsigned int countNumberOfRatings(Document.@NonNull Id documentId) {
        return (int) this.show().parallelStream()
            .filter(review -> review.getId().getFirstEntityId().equals(documentId))
            .count();
    }
}
