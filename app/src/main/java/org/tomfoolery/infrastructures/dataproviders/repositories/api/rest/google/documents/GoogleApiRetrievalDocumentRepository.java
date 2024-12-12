package org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.google.documents;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.documents.RetrievalDocumentRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;

import java.time.Instant;
import java.time.Year;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class GoogleApiRetrievalDocumentRepository implements RetrievalDocumentRepository {
    private static final Staff.@NonNull Id DECOY_STAFF_ID = Staff.Id.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    private static final @NonNull String ENDPOINT_URL = "https://www.googleapis.com/books/v1/volumes/";

    private final @NonNull HttpClientProvider httpClientProvider;

    @Override
    public @Nullable Document getById(Document.@NonNull Id entityId) {
        try {
            val httpResponse = this.httpClientProvider.sendSynchronousGET(
                String.format("%s?q=isbn:%s", ENDPOINT_URL, entityId.getISBN_10()),
                HttpClientProvider.Headers.builder().build()
            );

            return this.constructDocumentFromHttpResponse(httpResponse);

        } catch (Exception exception) {
            return null;
        }
    }

    private @Nullable Document constructDocumentFromHttpResponse(@NonNull String httpResponse) {
        val jsonResponse = JsonIterator.deserialize(httpResponse);

        val totalItems = jsonResponse.get("totalItems").toInt();
        if (totalItems < 1)
            return null;

        val volumeInfo = jsonResponse.get("items", 0, "volumeInfo");

        val ISBN = volumeInfo.get("industryIdentifiers", 0, "identifier").toString();

        val title = volumeInfo.get("title").toString();
        val description = volumeInfo.get("description").toString();
        val authors = volumeInfo.get("authors").asList().parallelStream()
            .map(Any::toString)
            .collect(Collectors.toUnmodifiableList());
        val genres = volumeInfo.get("categories").asList().parallelStream()
            .map(Any::toString)
            .collect(Collectors.toUnmodifiableList());

        val rawPublishedYear = volumeInfo.get("publishedDate").toString();
        val publishedYear = Year.parse(rawPublishedYear.split("-")[0]);
        val publisher = volumeInfo.get("publisher").toString();

        val documentCoverImageUrl = volumeInfo.get("imageLinks", "thumbnail").toString();
        val documentCoverImage = this.getDocumentCoverImageFromUrl(documentCoverImageUrl);

        val documentId = Document.Id.of(ISBN);
        assert documentId != null;

        val documentMetadata = Document.Metadata.of(title, description, authors, genres, publishedYear, publisher);
        val documentAudit = Document.Audit.of(Document.Audit.Timestamps.of(Instant.now()), DECOY_STAFF_ID);

        return Document.of(documentId, documentAudit, documentMetadata, null, documentCoverImage);
    }

    private Document.@Nullable CoverImage getDocumentCoverImageFromUrl(@NonNull String url) {
        try {
            val rawDocumentCoverImage = this.httpClientProvider.sendSynchronousGETForBytes(url, HttpClientProvider.Headers.builder().build());
            return Document.CoverImage.of(rawDocumentCoverImage);

        } catch (Exception exception) {
            return null;
        }
    }
}
