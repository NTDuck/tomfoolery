package org.tomfoolery.core.dataproviders.repositories.relations;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseBiRelationRepositoryTest;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.Review;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.infrastructures.utils.helpers.mockers.abc.EntityMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.relations.ReviewMocker;

public abstract class ReviewRepositoryTest extends BaseBiRelationRepositoryTest<Review, Review.Id, Document.Id, Patron.Id> {
    @Override
    protected abstract @NonNull ReviewRepository createTestSubject();

    @Override
    protected @NonNull EntityMocker<Review, Review.Id> createEntityMocker() {
        return ReviewMocker.of();
    }
}