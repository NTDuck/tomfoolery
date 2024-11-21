package org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;

import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    private static final @NonNull String URL_PARAMETER_NUMBER_OF_BORROWING_PATRONS = "patronCount";
    private static final @NonNull String URL_PARAMETER_RATING = "rating";
    private static final @NonNull String URL_PARAMETER_NUMBER_OF_RATINGS = "ratingCount";

    private static final @NonNull String DELIMITER = ",";

    @Override
    @SneakyThrows(URISyntaxException.class)
    public @NonNull String generateUrlFromFragmentaryDocument(@NonNull FragmentaryDocument fragmentaryDocument) {
        val parameterPairs = generateParameterPairsFromFragmentaryDocument(fragmentaryDocument);

        URIBuilder uriBuilder = new URIBuilder()
            .setHost(URL_HOST)
            .addParameters(parameterPairs);

        return uriBuilder.build().toString();
    }

    private static @NonNull List<NameValuePair> generateParameterPairsFromFragmentaryDocument(@NonNull FragmentaryDocument fragmentaryDocument) {
        return List.of(
            ParameterPair.of(URL_PARAMETER_ISBN, fragmentaryDocument.getId().getISBN()),
            ParameterPair.of(URL_PARAMETER_TITLE, fragmentaryDocument.getMetadata().getTitle()),
            ParameterPair.of(URL_PARAMETER_DESCRIPTION, fragmentaryDocument.getMetadata().getDescription()),
            ParameterPair.of(URL_PARAMETER_AUTHORS, String.join(DELIMITER, fragmentaryDocument.getMetadata().getAuthors())),
            ParameterPair.of(URL_PARAMETER_GENRES, String.join(DELIMITER, fragmentaryDocument.getMetadata().getGenres())),
            ParameterPair.of(URL_PARAMETER_PUBLISHED_YEAR, fragmentaryDocument.getMetadata().getPublishedYear().format(DateTimeFormatter.ISO_ORDINAL_DATE)),
            ParameterPair.of(URL_PARAMETER_PUBLISHER, fragmentaryDocument.getMetadata().getPublisher()),
            ParameterPair.of(URL_PARAMETER_NUMBER_OF_BORROWING_PATRONS, String.valueOf(fragmentaryDocument.getAudit().getBorrowingPatronIds().size())),
            ParameterPair.of(URL_PARAMETER_RATING, String.valueOf(fragmentaryDocument.getAudit().getRating().getValue())),
            ParameterPair.of(URL_PARAMETER_NUMBER_OF_RATINGS, String.valueOf(fragmentaryDocument.getAudit().getRating().getNumberOfRatings()))
        );
    }

    @Value(staticConstructor = "of")
    private static class ParameterPair implements NameValuePair {
        @NonNull String name;
        @NonNull String value;
    }
}
