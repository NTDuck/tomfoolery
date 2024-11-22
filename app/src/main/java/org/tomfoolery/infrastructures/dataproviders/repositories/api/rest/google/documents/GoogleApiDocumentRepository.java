package org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.google.documents;

import com.google.errorprone.annotations.DoNotCall;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.utils.dataclasses.documents.AverageRating;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;

import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class GoogleApiDocumentRepository implements DocumentRepository {
    private static final @NonNull String ENDPOINT_URL = "https://www.googleapis.com/books/v1/volumes/";

    private final @NonNull HttpClientProvider httpClientProvider;

    @Override
    @DoNotCall
    public @NonNull Set<Document> getSavedEntitiesSince(@NonNull Instant fromTimestamp) {
        throw new UnsupportedOperationException();
    }

    @Override
    @DoNotCall
    public @NonNull Set<Document> getDeletedEntitiesSince(@NonNull Instant fromTimestamp) {
        throw new UnsupportedOperationException();
    }

    @Override
    @DoNotCall
    public void save(@NonNull Document entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    @DoNotCall
    public void delete(Document.@NonNull Id entityId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable Document getById(Document.@NonNull Id entityId) {
        try {
            val httpResponse = this.httpClientProvider.sendSynchronousGET(
                String.format("%s?q=isbn:%s", ENDPOINT_URL, entityId.getISBN()),
                HttpClientProvider.Headers.builder().build()
            );

            return constructDocumentFromHttpResponse(httpResponse);

        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    @DoNotCall
    public @NonNull List<Document> show() {
        throw new UnsupportedOperationException();
    }

    private static @Nullable Document constructDocumentFromHttpResponse(@NonNull String httpResponse) {
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

        return Document.of(
            Document.Id.of(ISBN),
            Document.Content.of(new byte[0]),
            Document.Metadata.of(
                title, description, authors, genres,
                publishedYear, publisher, Document.Metadata.CoverImage.of(new byte[0])
            ),
            Document.Audit.of(
                Staff.Id.of(UUID.fromString("00000000-0000-0000-0000-000000000000")),
                AverageRating.of(),
                Document.Audit.Timestamps.of(Instant.now())
            )
        );
    }
}
