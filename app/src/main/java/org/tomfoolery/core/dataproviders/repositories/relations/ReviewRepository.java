package org.tomfoolery.core.dataproviders.repositories.relations;

import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseBiRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.Review;
import org.tomfoolery.core.domain.users.Patron;

public interface ReviewRepository extends BaseBiRepository<Review, Review.Id, Document.Id, Patron.Id> {
}
