package org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.abc;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.documents.RetrievalDocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;

import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class RestApiRetrievalDocumentRepository implements RetrievalDocumentRepository {
    private static final Staff.@NonNull Id MOCK_STAFF_ID = Staff.Id.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));

    private final @NonNull HttpClientProvider httpClientProvider;

    @Override
    public @Nullable Document getById(Document.@NonNull Id documentId) {
        try {
            val requestUrl = this.getRequestUrl(documentId);
            val requestHeaders = this.getRequestHeaders();

            val httpResponse = this.httpClientProvider.sendSynchronousGET(requestUrl, requestHeaders);
            val minimalDocument = this.constructMinimalDocumentFromHttpResponse(httpResponse);

            return this.mapMinimalDocumentToDocument(minimalDocument);

        } catch (Exception exception) {
            return null;
        }
    }

    private @Nullable Document mapMinimalDocumentToDocument(@NonNull MinimalDocument minimalDocument) {
        val documentId = Document.Id.of(minimalDocument.getISBN());

        if (documentId == null)
            return null;

        val documentPublishedYear = parsePublishedYear(minimalDocument.getPublishedYear());

        if (documentPublishedYear == null)
            return null;

        val documentAudit = createMockDocumentAudit();
        val documentMetadata = Document.Metadata.of(
            minimalDocument.getTitle(),
            minimalDocument.getDescription(),
            minimalDocument.getAuthors(),
            minimalDocument.getGenres(),
            documentPublishedYear,
            minimalDocument.getPublisher()
        );
        val documentCoverImage = this.getCoverImageFromCoverImageUrl(minimalDocument.getCoverImageUrl());

        return Document.of(
            documentId,
            documentAudit,
            documentMetadata,
            null,
            documentCoverImage
        );
    }

    protected abstract @NonNull String getRequestUrl(Document.@NonNull Id documentId);

    protected HttpClientProvider.@NonNull Headers getRequestHeaders() {
        return HttpClientProvider.Headers.builder().build();
    }

    protected HttpClientProvider.@NonNull Headers getCoverImageRequestHeaders() {
        return this.getRequestHeaders();
    }

    protected abstract @NonNull MinimalDocument constructMinimalDocumentFromHttpResponse(@NonNull String httpResponse);

    private static Document.@NonNull Audit createMockDocumentAudit() {
        val currentTimestamp = Instant.now();
        val documentAuditTimestamps = Document.Audit.Timestamps.of(currentTimestamp);

        return Document.Audit.of(documentAuditTimestamps, MOCK_STAFF_ID);
    }

    private static @Nullable Year parsePublishedYear(@NonNull String rawPublishedYear) {
        try {
            return Year.parse(rawPublishedYear);

        } catch (Exception exception) {
            return null;
        }
    }

    private Document.@Nullable CoverImage getCoverImageFromCoverImageUrl(@NonNull String coverImageUrl) {
        try {
            val requestHeaders = this.getCoverImageRequestHeaders();
            val rawDocumentCoverImage = this.httpClientProvider.sendSynchronousGETForBytes(coverImageUrl, requestHeaders);

            return Document.CoverImage.of(rawDocumentCoverImage);

        } catch (Exception exception) {
            return null;
        }
    }

    @Value
    @Builder
    protected static class MinimalDocument {
        @NonNull String ISBN;

        @NonNull String title;
        @NonNull String description;
        @NonNull List<String> authors;
        @NonNull List<String> genres;

        @NonNull String publishedYear;
        @NonNull String publisher;

        @NonNull String coverImageUrl;
    }
}
