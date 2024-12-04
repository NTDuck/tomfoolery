package org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.domain.documents.Document;

import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor(staticName = "of")
public class ApacheHttpClientDocumentUrlGenerator implements DocumentUrlGenerator {
    private static final @NonNull String URL_SCHEME = "https";
    private static final @NonNull String URL_HOST = "tomfoolery-landing-page.github.io";
    private static final @NonNull String URL_PATH = "tomfoolery-landing-page/";

    private static final @NonNull String URL_PARAMETER_ISBN_10 = "isbn_10";

    private static final @NonNull String URL_PARAMETER_TITLE = "title";
    private static final @NonNull String URL_PARAMETER_DESCRIPTION = "description";
    private static final @NonNull String URL_PARAMETER_AUTHORS = "authors";
    private static final @NonNull String URL_PARAMETER_GENRES = "genres";

    private static final @NonNull String URL_PARAMETER_PUBLISHED_YEAR = "year";
    private static final @NonNull String URL_PARAMETER_PUBLISHER = "publisher";

    private static final @NonNull String URL_PARAMETER_AVERAGE_RATING = "rating";
    private static final @NonNull String URL_PARAMETER_NUMBER_OF_RATINGS = "ratingCount";

    private static final @NonNull String DELIMITER = ",";

    @Override
    @SneakyThrows(URISyntaxException.class)
    public @NonNull String generateUrlFromDocument(@NonNull Document document) {
        val parameterPairs = generateParameterPairsFromFragmentaryDocument(document);

        URIBuilder uriBuilder = new URIBuilder()
            .setScheme(URL_SCHEME)
            .setHost(URL_HOST)
            .setPath(URL_PATH)

            .addParameters(parameterPairs);

        return uriBuilder.build().toString();
    }

    private static @NonNull List<NameValuePair> generateParameterPairsFromFragmentaryDocument(@NonNull Document document) {
        return List.of(
            ParameterPair.of(URL_PARAMETER_ISBN_10, document.getId().getISBN_10()),
            ParameterPair.of(URL_PARAMETER_TITLE, document.getMetadata().getTitle()),
            ParameterPair.of(URL_PARAMETER_DESCRIPTION, document.getMetadata().getDescription()),
            ParameterPair.of(URL_PARAMETER_AUTHORS, String.join(DELIMITER, document.getMetadata().getAuthors())),
            ParameterPair.of(URL_PARAMETER_GENRES, String.join(DELIMITER, document.getMetadata().getGenres())),
            ParameterPair.of(URL_PARAMETER_PUBLISHED_YEAR, document.getMetadata().getPublishedYear().format(DateTimeFormatter.ofPattern("yyyy"))),
            ParameterPair.of(URL_PARAMETER_PUBLISHER, document.getMetadata().getPublisher()),
            ParameterPair.of(URL_PARAMETER_AVERAGE_RATING, String.valueOf(document.getRating().getAverageRating())),
            ParameterPair.of(URL_PARAMETER_NUMBER_OF_RATINGS, String.valueOf(document.getRating().getNumberOfRatings()))
        );
    }

    @Value(staticConstructor = "of")
    private static class ParameterPair implements NameValuePair {
        @NonNull String name;
        @NonNull String value;
    }
}
