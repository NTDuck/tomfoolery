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
public class TomfooleryLandingPageDocumentUrlGenerator extends ApacheHttpClientDocumentUrlGenerator {
    private static final @NonNull CharSequence DELIMITER = ",";

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
            ParameterPair.of("ISBN 10", document.getId().getISBN_10()),
            ParameterPair.of("ISBN 13", document.getId().getISBN_13()),
            ParameterPair.of("title", document.getMetadata().getTitle()),
            ParameterPair.of("description", document.getMetadata().getDescription()),
            ParameterPair.of("authors", String.join(DELIMITER, document.getMetadata().getAuthors())),
            ParameterPair.of("genres", String.join(DELIMITER, document.getMetadata().getGenres())),
            ParameterPair.of("published year", document.getMetadata().getPublishedYear().format(DateTimeFormatter.ofPattern("yyyy"))),
            ParameterPair.of("publisher", document.getMetadata().getPublisher()),
            ParameterPair.of("average rating", document.getRating() == null ? "null"
                : String.valueOf(document.getRating().getAverageRating())),
            ParameterPair.of("number of ratings", document.getRating() == null ? "null"
                : String.valueOf(document.getRating().getNumberOfRatings()))
        );
    }

    @Value(staticConstructor = "of")
    private static class ParameterPair implements NameValuePair {
        @NonNull String name;
        @NonNull String value;
    }
}
