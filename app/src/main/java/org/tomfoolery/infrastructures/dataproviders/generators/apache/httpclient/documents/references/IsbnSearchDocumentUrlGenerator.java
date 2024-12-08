package org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references.abc.ApacheHttpClientDocumentUrlGenerator;

@NoArgsConstructor(staticName = "of")
public class IsbnSearchDocumentUrlGenerator extends ApacheHttpClientDocumentUrlGenerator {
    @Override
    protected @NonNull String getUrlScheme() {
        return "https";
    }

    @Override
    protected @NonNull String getUrlHost() {
        return "isbnsearch.org";
    }

    @Override
    protected @NonNull String getUrlPath(@NonNull Document document) {
        return String.format("isbn/%s", document.getId().getISBN_10());
    }
}