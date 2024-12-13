package org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.hathitrust.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.abc.RestApiRetrievalDocumentRepository;

public class HathiTrustBibliographyApiRetrievalDocumentRepository extends RestApiRetrievalDocumentRepository {
    public static @NonNull HathiTrustBibliographyApiRetrievalDocumentRepository of(@NonNull HttpClientProvider httpClientProvider) {
        return new HathiTrustBibliographyApiRetrievalDocumentRepository(httpClientProvider);
    }

    protected HathiTrustBibliographyApiRetrievalDocumentRepository(@NonNull HttpClientProvider httpClientProvider) {
        super(httpClientProvider);
    }

    @Override
    protected @NonNull String getRequestUrl(Document.@NonNull Id documentId) {
        return "";
    }

    @Override
    protected @NonNull MinimalDocument constructMinimalDocumentFromHttpResponse(@NonNull String httpResponse) {
        return null;
    }
}
