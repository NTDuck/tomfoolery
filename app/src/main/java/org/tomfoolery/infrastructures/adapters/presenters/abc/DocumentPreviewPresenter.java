package org.tomfoolery.infrastructures.adapters.presenters.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;

import java.util.List;

public abstract class DocumentPreviewPresenter {
    protected static @NonNull ViewModel generateViewModelFromDocumentPreview(Document.@NonNull Preview documentPreview) {
        val documentId = documentPreview.getId();
        val ISBN = documentId.getISBN();

        val documentMetadata = documentPreview.getMetadata();

        val title = documentMetadata.getTitle();
        val description = documentMetadata.getDescription();
        val authors = documentMetadata.getAuthors();
        val genres = documentMetadata.getGenres();

        val publishedYear = documentMetadata.getPublishedYear().getValue();
        val publisher = documentMetadata.getPublisher();

        val coverImage = documentMetadata.getCoverImage().getBuffer();

        val documentAudit = documentPreview.getAudit();

        val borrowingPatronCount = documentAudit.getBorrowingPatronCount();
        val ratingCount = documentAudit.getRating().getRatingCount();
        val rating = documentAudit.getRating().getRatingValue();

        return ViewModel.of(ISBN, title, description, authors, genres, publishedYear, publisher, borrowingPatronCount, ratingCount, rating, coverImage);
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull
        String ISBN;

        @NonNull String title;
        @NonNull String description;
        @NonNull
        List<String> authors;
        @NonNull List<String> genres;

        int publishedYear;
        @NonNull String publisher;

        int borrowingPatronCount;
        int ratingCount;
        double rating;

        byte @NonNull [] coverImage;
    }
}
