package org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.google.documents;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.abc.RestApiRetrievalDocumentRepository;

import java.util.stream.Collectors;

public class GoogleBooksApiRetrievalDocumentRepository extends RestApiRetrievalDocumentRepository {
    public static @NonNull GoogleBooksApiRetrievalDocumentRepository of(@NonNull HttpClientProvider httpClientProvider) {
        return new GoogleBooksApiRetrievalDocumentRepository(httpClientProvider);
    }

    protected GoogleBooksApiRetrievalDocumentRepository(@NonNull HttpClientProvider httpClientProvider) {
        super(httpClientProvider);
    }

    @Override
    protected @NonNull String getRequestUrl(Document.@NonNull Id documentId) {
        return "https://www.googleapis.com/books/v1/volumes/?q=isbn:%s"
            .formatted(documentId.getISBN_10());
    }

    protected @NonNull MinimalDocument constructMinimalDocumentFromHttpResponse(@NonNull String httpResponse) {
        val jsonResponse = JsonIterator.deserialize(httpResponse);

        val totalItems = jsonResponse.get("totalItems").toInt();
        assert totalItems > 1;

        val volumeInfo = jsonResponse.get("items", 0, "volumeInfo");

        return MinimalDocument.builder()
            .ISBN(volumeInfo.get("industryIdentifiers", 0, "identifier").toString())
            .title(volumeInfo.get("title").toString())
            .description(volumeInfo.get("description").toString())
            .authors(volumeInfo.get("authors").asList().parallelStream()
                .map(Any::toString)
                .collect(Collectors.toUnmodifiableList()))
            .genres(volumeInfo.get("categories").asList().parallelStream()
                .map(Any::toString)
                .collect(Collectors.toUnmodifiableList()))
            .publishedYear(volumeInfo.get("publishedDate").toString().split("-")[0])
            .publisher(volumeInfo.get("publisher").toString())
            .coverImageUrl(volumeInfo.get("imageLinks", "thumbnail").toString())
            
            .build();
    }
}
