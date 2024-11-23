package org.tomfoolery.infrastructures.utils.dataclasses;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;

import java.util.List;

@Value
@Builder(setterPrefix = "set", toBuilder = true, access = AccessLevel.PRIVATE)
public class ViewableFragmentaryDocument {
    @NonNull String ISBN;

    @NonNull String documentTitle;
    @NonNull String documentDescription;
    @NonNull List<String> documentAuthors;
    @NonNull List<String> documentGenres;

    @Unsigned short documentPublishedYear;
    @NonNull String documentPublisher;

    byte @NonNull [] documentCoverImage;

    @Unsigned long numberOfBorrowingPatrons;
    @Unsigned long numberOfRatings;
    @Unsigned double averageRating;

    public static @NonNull ViewableFragmentaryDocument of(@NonNull FragmentaryDocument fragmentaryDocument) {
        val documentId = fragmentaryDocument.getId();
        val documentMetadata = fragmentaryDocument.getMetadata();
        val documentAudit = fragmentaryDocument.getAudit();

        return ViewableFragmentaryDocument.builder()
            .setISBN(documentId.getISBN())

            .setDocumentTitle(documentMetadata.getTitle())
            .setDocumentDescription(documentMetadata.getDescription())
            .setDocumentAuthors(documentMetadata.getAuthors())
            .setDocumentGenres(documentMetadata.getGenres())

            .setDocumentPublishedYear((short) documentMetadata.getPublishedYear().getValue())
            .setDocumentPublisher(documentMetadata.getPublisher())

            .setNumberOfBorrowingPatrons(documentAudit.getBorrowingPatronIds().size())
            .setNumberOfRatings(documentAudit.getRating().getNumberOfRatings())
            .setAverageRating(documentAudit.getRating().getValue())

            .build();
    }
}