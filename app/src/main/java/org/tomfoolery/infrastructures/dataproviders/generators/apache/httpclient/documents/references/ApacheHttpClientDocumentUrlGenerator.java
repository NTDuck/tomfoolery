package org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.utils.dataclasses.documents.AverageRating;

import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

@NoArgsConstructor(staticName = "of")
public class ApacheHttpClientDocumentUrlGenerator implements DocumentUrlGenerator {
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
    public @NonNull String generateUrlFromFragmentaryDocument(@NonNull FragmentaryDocument fragmentaryDocument) {
        val parameterPairs = generateParameterPairsFromDocumentPreview(fragmentaryDocument);

        URIBuilder uriBuilder = new URIBuilder()
            .setHost(URL_HOST)
            .addParameters(parameterPairs);

        return uriBuilder.build().toString();
    }

    private static @NonNull List<NameValuePair> generateParameterPairsFromDocumentPreview(@NonNull FragmentaryDocument fragmentaryDocument) {
        return List.of(
            generateParameterPairFromDocumentISBN(fragmentaryDocument.getId().getISBN()),
            generateParameterPairFromDocumentTitle(fragmentaryDocument.getMetadata().getTitle()),
            generateParameterPairFromDocumentDescription(fragmentaryDocument.getMetadata().getDescription()),
            generateParameterPairFromDocumentAuthors(fragmentaryDocument.getMetadata().getAuthors()),
            generateParameterPairFromDocumentGenres(fragmentaryDocument.getMetadata().getGenres()),
            generateParameterPairFromDocumentPublishedYear(fragmentaryDocument.getMetadata().getPublishedYear()),
            generateParameterPairFromDocumentPublisher(fragmentaryDocument.getMetadata().getPublisher()),
            generateParameterPairFromNumberOfBorrowingPatrons(fragmentaryDocument.getAudit().getBorrowingPatronIds().size()),
            generateParameterPairFromDocumentRating(fragmentaryDocument.getAudit().getRating())
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

    private static @NonNull ParameterPair generateParameterPairFromNumberOfBorrowingPatrons(@Unsigned int numberOfBorrowingPatrons) {
        val stringifiedNumberOfBorrowingPatrons = Integer.valueOf(numberOfBorrowingPatrons).toString();
        return ParameterPair.of(URL_PARAMETER_BORROWING_PATRON_COUNT, stringifiedNumberOfBorrowingPatrons);
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
