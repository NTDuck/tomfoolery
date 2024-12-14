package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.relations;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.Review;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

import java.util.UUID;

import static org.testng.Assert.*;
import static org.testng.AssertJUnit.fail;

@Test(groups = { "unit", "repository", "bi-relation", "cloud" }, dependsOnGroups = { "configurations" })
public class CloudReviewRepositoryTest extends BaseUnitTest<CloudReviewRepository> {
    @Override
    protected @NonNull CloudReviewRepository createTestSubject() {
        DotenvProvider dotenvProvider = CdimascioDotenvProvider.of();
        CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider = CloudDatabaseConfigurationsProvider.of(dotenvProvider);
        return CloudReviewRepository.of(cloudDatabaseConfigurationsProvider);
    }

    @Test
    public void WhenSavingReview_ExpectReviewToBeSavedSuccessfully() {
        Review.Id reviewId = Review.Id.of(
                Document.Id.of("123456789X"),
                Patron.Id.of(UUID.randomUUID())
        );
        Review review = Review.of(reviewId, 4.5);

        try {
            this.testSubject.save(review);

            Review retrievedReview = this.testSubject.getById(reviewId);
            assertNotNull(retrievedReview, "Saved review should not be null");
            assertEquals(retrievedReview.getRating(), 4.5, "Review rating should match the saved value");
            assertEquals(retrievedReview.getId(), reviewId, "Review ID should match the saved value");

        } catch (Exception e) {
            fail("Saving review failed: " + e.getMessage());
        }
    }

    @Test(dependsOnMethods = { "WhenSavingReview_ExpectReviewToBeSavedSuccessfully" })
    public void GivenExistingReview_WhenDeletingReview_ExpectReviewToBeDeleted() {
        Review.Id reviewId = Review.Id.of(
                Document.Id.of("123456789X"),  // Example ISBN-10
                Patron.Id.of(UUID.randomUUID())
        );
        Review review = Review.of(reviewId, 4.5);

        try {
            this.testSubject.save(review);

            this.testSubject.delete(reviewId);

            Review retrievedReview = this.testSubject.getById(reviewId);
            assertNull(retrievedReview, "Deleted review should not exist");

        } catch (Exception e) {
            fail("Deleting review failed: " + e.getMessage());
        }
    }

    @Test
    public void WhenQueryingAllReviews_ExpectAllSavedReviewsToBeReturned() {
        Review review1 = Review.of(
                Review.Id.of(
                        Document.Id.of("123456789X"),  // Example ISBN-10
                        Patron.Id.of(UUID.randomUUID())
                ),
                4.5
        );

        Review review2 = Review.of(
                Review.Id.of(
                        Document.Id.of("0198534531"),  // Example ISBN-10
                        Patron.Id.of(UUID.randomUUID())
                ),
                3.8
        );

        try {
            this.testSubject.save(review1);
            this.testSubject.save(review2);

            val reviews = this.testSubject.show();

            assertNotNull(reviews, "Reviews list should not be null");
            assertTrue(reviews.size() >= 2, "There should be at least two reviews in the database");

            assertTrue(reviews.contains(review1), "Review1 should be in the list of reviews");
            assertTrue(reviews.contains(review2), "Review2 should be in the list of reviews");

        } catch (Exception e) {
            fail("Querying all reviews failed: " + e.getMessage());
        }
    }
}
