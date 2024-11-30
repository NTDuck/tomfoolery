package org.tomfoolery.core.dataproviders.generators.documents.search;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.List;

public interface DocumentSearchGenerator extends BaseSynchronizedGenerator<Document, Document.Id> {
    @NonNull List<Document> searchByTitle(@NonNull String title);
    @NonNull List<Document> searchByAuthor(@NonNull String author);
    @NonNull List<Document> searchByGenre(@NonNull String genre);

    default @Nullable Page<DocumentWithoutContent> searchPaginatedDocumentsByTitlePrefix(@NonNull String title, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocumentWithoutContents = this.searchDocumentsByTitlePrefix(title);
        return Page.fromUnpaginated(unpaginatedDocumentWithoutContents, pageIndex, maxPageSize);
    }

    default @Nullable Page<DocumentWithoutContent> searchPaginatedDocumentsByTitleSuffix(@NonNull String title, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocumentWithoutContents = this.searchDocumentsByTitleSuffix(title);
        return Page.fromUnpaginated(unpaginatedDocumentWithoutContents, pageIndex, maxPageSize);
    }

    default @Nullable Page<DocumentWithoutContent> searchPaginatedDocumentsByTitleSubsequence(@NonNull String title, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocumentWithoutContents = this.searchByTitle(title);
        return Page.fromUnpaginated(unpaginatedDocumentWithoutContents, pageIndex, maxPageSize);
    }

    default @Nullable Page<DocumentWithoutContent> searchPaginatedDocumentsByAuthorPrefix(@NonNull String author, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocumentWithoutContents = this.searchDocumentsByAuthorPrefix(author);
        return Page.fromUnpaginated(unpaginatedDocumentWithoutContents, pageIndex, maxPageSize);
    }

    default @Nullable Page<DocumentWithoutContent> searchPaginatedDocumentsByAuthorSuffix(@NonNull String author, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocumentWithoutContents = this.searchDocumentsByAuthorSuffix(author);
        return Page.fromUnpaginated(unpaginatedDocumentWithoutContents, pageIndex, maxPageSize);
    }

    default @Nullable Page<DocumentWithoutContent> searchPaginatedDocumentsByAuthorSubsequence(@NonNull String author, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocumentWithoutContents = this.searchByAuthor(author);
        return Page.fromUnpaginated(unpaginatedDocumentWithoutContents, pageIndex, maxPageSize);
    }

    default @Nullable Page<DocumentWithoutContent> searchPaginatedDocumentsByGenrePrefix(@NonNull String genre, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocumentWithoutContents = this.searchDocumentsByGenrePrefix(genre);
        return Page.fromUnpaginated(unpaginatedDocumentWithoutContents, pageIndex, maxPageSize);
    }

    default @Nullable Page<DocumentWithoutContent> searchPaginatedDocumentsByGenreSuffix(@NonNull String genre, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocumentWithoutContents = this.searchDocumentsByGenreSuffix(genre);
        return Page.fromUnpaginated(unpaginatedDocumentWithoutContents, pageIndex, maxPageSize);
    }

    default @Nullable Page<DocumentWithoutContent> searchPaginatedDocumentsByGenreSubsequence(@NonNull String genre, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocumentWithoutContents = this.searchByGenre(genre);
        return Page.fromUnpaginated(unpaginatedDocumentWithoutContents, pageIndex, maxPageSize);
    }
}
