package org.tomfoolery.infrastructures.utils.helpers.mockers.documents;

import com.github.javafaker.Faker;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.patron.documents.review.persistence.AddDocumentReviewUseCase;
import org.tomfoolery.infrastructures.utils.helpers.mockers.abc.EntityMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.common.TimestampsMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.StaffMocker;

import java.time.Year;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(staticName = "of")
public class DocumentMocker implements EntityMocker<Document, Document.Id> {
    private final @Unsigned int MIN_NUMBER_OF_AUTHORS = 1;
    private final @Unsigned int MAX_NUMBER_OF_AUTHORS = 14;
    private final @Unsigned int MIN_NUMBER_OF_GENRES = 1;
    private final @Unsigned int MAX_NUMBER_OF_GENRES = 14;
    private final @Unsigned int MIN_PUBLISHED_YEAR = 444;
    private final @Unsigned int MAX_PUBLISHED_YEAR = 2444;
    private final @Unsigned int MAX_DECIMALS_OF_RATINGS = 4;
    private final @Unsigned int MIN_AVERAGE_RATING = AddDocumentReviewUseCase.MIN_RATING;
    private final @Unsigned int MAX_AVERAGE_RATING = AddDocumentReviewUseCase.MAX_RATING;
    private final @Unsigned int MIN_NUMBER_OF_RATINGS = 1;
    private final @Unsigned int MAX_NUMBER_OF_RATINGS = 4444;

    private final @NonNull Faker faker = Faker.instance();

    private final @NonNull StaffMocker staffMocker = StaffMocker.of();
    private final @NonNull TimestampsMocker timestampsMocker = TimestampsMocker.of();

    @Override
    public Document.@NonNull Id createMockEntityId() {
        val ISBN_10 = this.faker.code().isbn10();
        val documentId = Document.Id.of(ISBN_10);

        assert documentId != null;
        return documentId;
    }

    @Override
    public @NonNull Document createMockEntityWithId(Document.@NonNull Id documentId) {
        val title = this.faker.book().title();
        val description = this.faker.leagueOfLegends().quote();
        val authors = createMockCollection(() -> this.faker.book().author(),
            this.faker.number().numberBetween(MIN_NUMBER_OF_AUTHORS, MAX_NUMBER_OF_AUTHORS));
        val genres = createMockCollection(() -> this.faker.book().genre(),
            this.faker.number().numberBetween(MIN_NUMBER_OF_GENRES, MAX_NUMBER_OF_GENRES));
        val publishedYear = Year.of(this.faker.number().numberBetween(MIN_PUBLISHED_YEAR, MAX_PUBLISHED_YEAR));
        val publisher = this.faker.book().publisher();
        val averageRating = this.faker.number().randomDouble(MAX_DECIMALS_OF_RATINGS, MIN_AVERAGE_RATING, MAX_AVERAGE_RATING);
        val numberOfRatings = this.faker.number().numberBetween(MIN_NUMBER_OF_RATINGS, MAX_NUMBER_OF_RATINGS);

        return Document.of(
            documentId,
            Document.Audit.of(
                Document.Audit.Timestamps.of(this.timestampsMocker.createMockTimestamps()),
                this.staffMocker.createMockEntityId()
            ),
            Document.Metadata.of(
                title, description, authors, genres,
                publishedYear, publisher
            ),
            Document.Rating.of(averageRating, numberOfRatings),
            null
        );
    }

    private static <T> @NonNull List<T> createMockCollection(@NonNull Supplier<T> supplier, @Unsigned int size) {
        return Stream.generate(supplier).parallel()
            .limit(size)
            .collect(Collectors.toUnmodifiableList());
    }
}
