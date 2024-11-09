package org.tomfoolery.infrastructures.dataproviders.generators.apache.document.references;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.utils.dataclasses.documents.AverageRating;

import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor(staticName = "of")
public class ApacheDocumentUrlGenerator implements DocumentUrlGenerator {
    private static final @NonNull String URL_HOST = "";

    private static final @NonNull String URL_PARAMETER_ISBN = "isbn";

    private static final @NonNull String URL_PARAMETER_TITLE = "title";
    private static final @NonNull String URL_PARAMETER_DESCRIPTION = "description";
    private static final @NonNull String URL_PARAMETER_AUTHORS = "authors";
    private static final @NonNull String URL_PARAMETER_GENRES = "genres";

    private static final @NonNull String URL_PARAMETER_PUBLISHED_YEAR = "year";
    private static final @NonNull String URL_PARAMETER_PUBLISHER = "publisher";

    private static final @NonNull String URL_PARAMETER_BORROWING_PATRON_COUNT = "patronCount";
    private static final @NonNull String URL_PARAMETER_RATING = "rating";

    private static final @NonNull String DELIMITER = ",";

    @Override
    @SneakyThrows
    public @NonNull String generateUrlFromDocumentPreview(Document.@NonNull Preview documentPreview) {
        val parameterPairs = generateParameterPairsFromDocumentPreview(documentPreview);

        URIBuilder uriBuilder = new URIBuilder()
            .setHost(URL_HOST)
            .addParameters(parameterPairs);

        return uriBuilder.build().toString();
    }

    private static @NonNull List<NameValuePair> generateParameterPairsFromDocumentPreview(Document.@NonNull Preview documentPreview) {
        return List.of(
            generateParameterPairFromDocumentISBN(documentPreview.getId().getISBN()),
            generateParameterPairFromDocumentTitle(documentPreview.getMetadata().getTitle()),
            generateParameterPairFromDocumentDescription(documentPreview.getMetadata().getDescription()),
            generateParameterPairFromDocumentAuthors(documentPreview.getMetadata().getAuthors()),
            generateParameterPairFromDocumentGenres(documentPreview.getMetadata().getGenres()),
            generateParameterPairFromDocumentPublishedYear(documentPreview.getMetadata().getPublishedYear()),
            generateParameterPairFromDocumentPublisher(documentPreview.getMetadata().getPublisher()),
            generateParameterPairFromDocumentBorrowingPatronCount(documentPreview.getAudit().getBorrowingPatronCount()),
            generateParameterPairFromDocumentRating(documentPreview.getAudit().getRating())
        );
    }

    private static @NonNull ParameterPair generateParameterPairFromDocumentISBN(@NonNull String ISBN) {
        return ParameterPair.of(URL_PARAMETER_ISBN, ISBN);
    }

    private static @NonNull ParameterPair generateParameterPairFromDocumentTitle(@NonNull String title) {
        return ParameterPair.of(URL_PARAMETER_TITLE, title);
    }

    private static @NonNull ParameterPair generateParameterPairFromDocumentDescription(@NonNull String description) {
        return ParameterPair.of(URL_PARAMETER_DESCRIPTION, description);
    }

    private static @NonNull ParameterPair generateParameterPairFromDocumentAuthors(@NonNull List<String> authors) {
        val authorsJoined = String.join(DELIMITER, authors);
        return ParameterPair.of(URL_PARAMETER_AUTHORS, authorsJoined);
    }

    private static @NonNull ParameterPair generateParameterPairFromDocumentGenres(@NonNull List<String> genres) {
        val genresJoined = String.join(DELIMITER, genres);
        return ParameterPair.of(URL_PARAMETER_GENRES, genresJoined);
    }

    private static @NonNull ParameterPair generateParameterPairFromDocumentPublishedYear(@NonNull Year publishedYear) {
        val formattedPublishedYear = publishedYear.format(DateTimeFormatter.ISO_ORDINAL_DATE);
        return ParameterPair.of(URL_PARAMETER_PUBLISHED_YEAR, formattedPublishedYear);
    }

    private static @NonNull ParameterPair generateParameterPairFromDocumentPublisher(@NonNull String publisher) {
        return ParameterPair.of(URL_PARAMETER_PUBLISHER, publisher);
    }

    private static @NonNull ParameterPair generateParameterPairFromDocumentBorrowingPatronCount(int borrowingPatronCount) {
        val stringifiedBorrowingPatronCount = Integer.valueOf(borrowingPatronCount).toString();
        return ParameterPair.of(URL_PARAMETER_BORROWING_PATRON_COUNT, stringifiedBorrowingPatronCount);
    }

    private static @NonNull ParameterPair generateParameterPairFromDocumentRating(@NonNull AverageRating rating) {
        val stringifiedRating = Double.valueOf(rating.getValue()).toString();
        return ParameterPair.of(URL_PARAMETER_RATING, stringifiedRating);
    }

    @Value(staticConstructor = "of")
    private static class ParameterPair implements NameValuePair {
        @NonNull String name;
        @NonNull String value;
    }
}
