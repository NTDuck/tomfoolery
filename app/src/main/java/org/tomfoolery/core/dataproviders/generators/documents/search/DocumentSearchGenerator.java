package org.tomfoolery.core.dataproviders.generators.documents.search;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSearchGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.List;

public interface DocumentSearchGenerator extends BaseSearchGenerator<Document, Document.Id> {
    @NonNull List<Document> searchByNormalizedTitle(@NonNull String normalizedTitle);
    @NonNull List<Document> searchByNormalizedAuthor(@NonNull String normalizedAuthor);
    @NonNull List<Document> searchByNormalizedGenre(@NonNull String normalizedGenre);

    default @NonNull List<Document> searchByTitle(@NonNull String title) {
        return this.searchByNormalizedCriterion(this::searchByNormalizedTitle, title);
    }

    default @NonNull List<Document> searchByAuthor(@NonNull String author) {
        return this.searchByNormalizedCriterion(this::searchByNormalizedAuthor, author);
    }

    default @NonNull List<Document> searchByGenre(@NonNull String genre) {
        return this.searchByNormalizedCriterion(this::searchByNormalizedGenre, genre);
    }

    default @Nullable Page<Document> searchPageByTitle(@NonNull String title, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocuments = this.searchByTitle(title);
        return Page.fromUnpaginated(unpaginatedDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<Document> searchPageByAuthor(@NonNull String author, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocuments = this.searchByAuthor(author);
        return Page.fromUnpaginated(unpaginatedDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<Document> searchPageByGenre(@NonNull String genre, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocuments = this.searchByGenre(genre);
        return Page.fromUnpaginated(unpaginatedDocuments, pageIndex, maxPageSize);
    }
}
