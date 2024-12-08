package org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references;

import lombok.NoArgsConstructor;
import lombok.Value;
import org.apache.hc.core5.http.NameValuePair;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references.abc.ApacheHttpClientDocumentUrlGenerator;

import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor(staticName = "of")
public class CustomLandingPageDocumentUrlGenerator extends ApacheHttpClientDocumentUrlGenerator {
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
    protected @NonNull String getUrlScheme() {
        return "https";
    }

    @Override
    protected @NonNull String getUrlHost() {
        return "tomfoolery-landing-page.github.io";
    }

    @Override
    protected @NonNull String getUrlPath(@NonNull Document document) {
        return "tomfoolery-landing-page";
    }

    @Override
    protected @NonNull List<NameValuePair> getUrlParameterPairs(@NonNull Document document) {
        return List.of(
            ParameterPair.of(URL_PARAMETER_ISBN_10, document.getId().getISBN_10()),
            ParameterPair.of(URL_PARAMETER_TITLE, document.getMetadata().getTitle()),
            ParameterPair.of(URL_PARAMETER_DESCRIPTION, document.getMetadata().getDescription()),
            ParameterPair.of(URL_PARAMETER_AUTHORS, String.join(DELIMITER, document.getMetadata().getAuthors())),
            ParameterPair.of(URL_PARAMETER_GENRES, String.join(DELIMITER, document.getMetadata().getGenres())),
            ParameterPair.of(URL_PARAMETER_PUBLISHED_YEAR, document.getMetadata().getPublishedYear().format(DateTimeFormatter.ofPattern("yyyy"))),
            ParameterPair.of(URL_PARAMETER_PUBLISHER, document.getMetadata().getPublisher()),
            ParameterPair.of(URL_PARAMETER_AVERAGE_RATING, document.getRating() == null ? "null"
                : String.valueOf(document.getRating().getAverageRating())),
            ParameterPair.of(URL_PARAMETER_NUMBER_OF_RATINGS, document.getRating() == null ? "null"
                : String.valueOf(document.getRating().getNumberOfRatings()))
        );
    }

    @Value(staticConstructor = "of")
    private static class ParameterPair implements NameValuePair {
        @NonNull String name;
        @NonNull String value;
    }
}
