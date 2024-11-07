package org.tomfoolery.infrastructures.utils.dataclasses;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;

import java.util.List;

@Value
public class ViewonlyDocumentPreview {
    @NonNull String ISBN;

    @NonNull String title;
    @NonNull String description;
    @NonNull List<String> authors;
    @NonNull List<String> genres;

    int publishedYear;
    @NonNull String publisher;

    int borrowingPatronCount;
    int ratingCount;
    double rating;

    byte @NonNull [] coverImage;

    public static @NonNull ViewonlyDocumentPreview of(Document.@NonNull Preview documentPreview) {
        return new ViewonlyDocumentPreview(documentPreview);
    }

    private ViewonlyDocumentPreview(Document.@NonNull Preview documentPreview) {
        val documentId = documentPreview.getId();
        this.ISBN = documentId.getISBN();

        val documentMetadata = documentPreview.getMetadata();

        this.title = documentMetadata.getTitle();
        this.description = documentMetadata.getDescription();
        this.authors = documentMetadata.getAuthors();
        this.genres = documentMetadata.getGenres();

        this.publishedYear = documentMetadata.getPublishedYear().getValue();
        this.publisher = documentMetadata.getPublisher();

        this.coverImage = documentMetadata.getCoverImage().getBuffer();

        val documentAudit = documentPreview.getAudit();

        this.borrowingPatronCount = documentAudit.getBorrowingPatronCount();
        this.ratingCount = documentAudit.getRating().getRatingCount();
        this.rating = documentAudit.getRating().getRatingValue();
    }
}
