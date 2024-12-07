package org.tomfoolery.core.dataproviders.repositories.documents;

import com.github.javafaker.Faker;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepositoryTest;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.patron.documents.review.persistence.AddDocumentReviewUseCase;

import java.time.Year;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "document" }, singleThreaded = true)
public abstract class DocumentRepositoryTest extends BaseRepositoryTest<Document, Document.Id> {
    private final @NonNull Faker faker = Faker.instance();

    @Override
    protected abstract @NonNull DocumentRepository createTestSubject();

    @Override
    protected Document.@NonNull Id createRandomMockEntityId() {
        val documentId = Document.Id.of(this.faker.code().isbn10());
        assert documentId != null;

        return documentId;
    }

    @Override
    protected @NonNull Document createMockEntityFromId(Document.@NonNull Id entityId) {
        return Document.of(
            this.createMockDocumentId(),
            Document.Audit.of(
                Document.Audit.Timestamps.of(this.faker.date().past(DAYS_CREATED_WITHIN, TimeUnit.DAYS).toInstant()),
                this.createMockUserId()
            ),
            Document.Metadata.of(this.faker.book().title(), this.faker.lorem().paragraph(),
                Stream.generate(() -> this.faker.book().author())
                    .limit(this.faker.number().numberBetween(MIN_NUMBER_OF_AUTHORS, MAX_NUMBER_OF_AUTHORS))
                    .collect(Collectors.toUnmodifiableList()),
                Stream.generate(() -> this.faker.book().genre())
                    .limit(this.faker.number().numberBetween(MIN_NUMBER_OF_GENRES, MAX_NUMBER_OF_GENRES))
                    .collect(Collectors.toUnmodifiableList()),
                Year.of(this.faker.number().numberBetween(MIN_PUBLISHED_YEAR, MAX_PUBLISHED_YEAR)),
                this.faker.book().publisher()
            ),
            Document.Rating.of(
                this.faker.number().randomDouble(MAX_DECIMALS_OF_RATINGS, AddDocumentReviewUseCase.MIN_RATING, AddDocumentReviewUseCase.MAX_RATING),
                this.faker.number().numberBetween(MIN_NUMBER_OF_RATINGS, MAX_NUMBER_OF_RATINGS)
            ),
            null
        );
    }
}