package org.tomfoolery.infrastructures.utils.helpers.mockers.relations;

import com.github.javafaker.Faker;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.domain.relations.Review;
import org.tomfoolery.core.usecases.patron.documents.review.persistence.AddDocumentReviewUseCase;
import org.tomfoolery.infrastructures.utils.helpers.mockers.abc.EntityMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.documents.DocumentMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.PatronMocker;

@NoArgsConstructor(staticName = "of")
public class ReviewMocker implements EntityMocker<Review, Review.Id> {
    private final @Unsigned int MAX_DECIMALS_OF_RATINGS = 4;
    private final @Unsigned int MIN_RATING = AddDocumentReviewUseCase.MIN_RATING;
    private final @Unsigned int MAX_RATING = AddDocumentReviewUseCase.MAX_RATING;

    private final @NonNull Faker faker = Faker.instance();

    private final @NonNull DocumentMocker documentMocker = DocumentMocker.of();
    private final @NonNull PatronMocker patronMocker = PatronMocker.of();

    @Override
    public Review.@NonNull Id createMockEntityId() {
        val documentId = this.documentMocker.createMockEntityId();
        val patronId = this.patronMocker.createMockEntityId();

        return Review.Id.of(documentId, patronId);
    }

    @Override
    public @NonNull Review createMockEntityWithId(Review.@NonNull Id reviewId) {
        val rating = this.faker.number().randomDouble(MAX_DECIMALS_OF_RATINGS, MIN_RATING, MAX_RATING);

        return Review.of(reviewId, rating);
    }
}
