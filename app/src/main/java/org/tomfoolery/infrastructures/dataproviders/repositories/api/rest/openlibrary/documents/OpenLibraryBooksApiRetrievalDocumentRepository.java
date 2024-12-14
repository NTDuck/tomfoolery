package org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.openlibrary.documents;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.abc.RestApiRetrievalDocumentRepository;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OpenLibraryBooksApiRetrievalDocumentRepository extends RestApiRetrievalDocumentRepository {
    public static @NonNull OpenLibraryBooksApiRetrievalDocumentRepository of(@NonNull HttpClientProvider httpClientProvider) {
        return new OpenLibraryBooksApiRetrievalDocumentRepository(httpClientProvider);
    }

    protected OpenLibraryBooksApiRetrievalDocumentRepository(@NonNull HttpClientProvider httpClientProvider) {
        super(httpClientProvider);
    }

    @Override
    protected @NonNull String getRequestUrl(Document.@NonNull Id documentId) {
        return "https://openlibrary.org/api/books?bibkeys=ISBN:%s&format=json&jscmd=data"
            .formatted(documentId.getISBN_10());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected @NonNull MinimalDocument constructMinimalDocumentFromHttpResponse(@NonNull String httpResponse) {
        val jsonResponse = JsonIterator.deserialize(httpResponse);

        val firstKey = jsonResponse.keys().iterator().next();
        val documentInfo = jsonResponse.get(firstKey);

        return MinimalDocument.builder()
            .ISBN(documentInfo.get("identifiers", "isbn_10", 0).toString())
            .title(documentInfo.get("title").toString())
            .description(documentInfo.get("excerpts", 0, "text").toString())
            .authors(documentInfo.get("authors").asList().parallelStream()
                .map(any -> any.get("name"))
                .map(Any::toString)
                .collect(Collectors.toUnmodifiableList()))
            .genres(documentInfo.get("subjects").asList().parallelStream()
                .map(any -> any.get("name"))
                .map(Any::toString)
                .collect(Collectors.toUnmodifiableList()))
            .publishedYear(extractLeftmostNumberOnlySubstring(documentInfo.get("publish_date").toString()))
            .publisher(documentInfo.get("publishers", 0, "name").toString())
            .coverImageUrl(documentInfo.get("cover", "large").toString())

            .build();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static @NonNull String extractLeftmostNumberOnlySubstring(@NonNull String s) {
        val pattern = Pattern.compile("\\d+");
        val matcher = pattern.matcher(s);

        matcher.find();
        assert matcher.hasMatch();

        return matcher.group();
    }
}
