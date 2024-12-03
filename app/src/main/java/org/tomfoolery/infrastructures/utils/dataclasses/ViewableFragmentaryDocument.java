package org.tomfoolery.infrastructures.utils.dataclasses;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.TemporaryFileProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.resources.ResourceProvider;

import java.io.IOException;
import java.util.List;

@Value
@Builder(setterPrefix = "set", toBuilder = true, access = AccessLevel.PRIVATE)
public class ViewableFragmentaryDocument {
    private static final @NonNull String DEFAULT_COVER_IMAGE_RESOURCE_PATH = "images/default/document-cover-image.png";

    @NonNull String ISBN;

    @NonNull String documentTitle;
    @NonNull String documentDescription;
    @NonNull List<String> documentAuthors;
    @NonNull List<String> documentGenres;

    @Unsigned short documentPublishedYear;
    @NonNull String documentPublisher;

    @NonNull String documentCoverImageFilePath;

    @Unsigned long numberOfBorrowingPatrons;
    @Unsigned long numberOfRatings;
    @Unsigned double averageRating;

    @NonNull String lastCreatedTimestamp;
    @NonNull String lastModifiedTimestamp;

    public static @NonNull ViewableFragmentaryDocument of(@NonNull FragmentaryDocument fragmentaryDocument) {
        val documentId = fragmentaryDocument.getId();
        val documentMetadata = fragmentaryDocument.getMetadata();
        val documentAudit = fragmentaryDocument.getAudit();

        val documentCoverImageFilePath = saveDocumentCoverImageAndGetPath(documentMetadata.getCoverImage().getBytes());

        return ViewableFragmentaryDocument.builder()
            .setISBN(documentId.getISBN())

            .setDocumentTitle(documentMetadata.getTitle())
            .setDocumentDescription(documentMetadata.getDescription())
            .setDocumentAuthors(documentMetadata.getAuthors())
            .setDocumentGenres(documentMetadata.getGenres())

            .setDocumentPublishedYear((short) documentMetadata.getPublishedYear().getValue())
            .setDocumentPublisher(documentMetadata.getPublisher())

            .setDocumentCoverImageFilePath(documentCoverImageFilePath)

            .setNumberOfBorrowingPatrons(documentAudit.getBorrowingPatronIds().size())
            .setNumberOfRatings(documentAudit.getRating().getNumberOfRatings())
            .setAverageRating(documentAudit.getRating().getValue())

            .setLastCreatedTimestamp(String.valueOf(documentAudit.getTimestamps().getCreated()))
            .setLastModifiedTimestamp(String.valueOf(documentAudit.getTimestamps().getLastModified()))

            .build();
    }

    @SneakyThrows
    private static @NonNull String saveDocumentCoverImageAndGetPath(byte @NonNull [] rawDocumentCoverImage) {
        try {
            return TemporaryFileProvider.save(".png", rawDocumentCoverImage);
        } catch (IOException exception) {
            return ResourceProvider.getResourceAbsolutePath(DEFAULT_COVER_IMAGE_RESOURCE_PATH);
        }
    }
}