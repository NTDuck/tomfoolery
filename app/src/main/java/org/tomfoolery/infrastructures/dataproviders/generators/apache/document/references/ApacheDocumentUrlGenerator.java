package org.tomfoolery.infrastructures.dataproviders.generators.apache.document.references;

import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.domain.documents.Document;

public class ApacheDocumentUrlGenerator implements DocumentUrlGenerator {
    private static final @NonNull String URL_HOST = "";

    private static final @NonNull String URL_PARAMETER_TITLE = "title";
    private static final @NonNull String URL_PARAMETER_DESCRIPTION = "description";
    private static final @NonNull String URL_PARAMETER_AUTHORS = "authors";
    private static final @NonNull String URL_PARAMETER_GENRES = "genres";

    @Override
    @SneakyThrows
    public @NonNull String generateUrlFromDocumentPreview(Document.@NonNull Preview documentPreview) {
        URIBuilder uriBuilder = new URIBuilder()
            .setHost(URL_HOST);

        return uriBuilder.build().toString();
    }
}
