package org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.openlibrary.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.abc.RestApiRetrievalDocumentRepository;

public class OpenLibraryApiRetrievalDocumentRepository extends RestApiRetrievalDocumentRepository {
    public static @NonNull OpenLibraryApiRetrievalDocumentRepository of(@NonNull HttpClientProvider httpClientProvider) {
        return new OpenLibraryApiRetrievalDocumentRepository(httpClientProvider);
    }

    protected OpenLibraryApiRetrievalDocumentRepository(@NonNull HttpClientProvider httpClientProvider) {
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
