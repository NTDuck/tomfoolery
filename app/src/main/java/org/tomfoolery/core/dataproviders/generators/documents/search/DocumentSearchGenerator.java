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

    default @Nullable Page<Document> searchPaginatedByTitle(@NonNull String title, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocuments = this.searchByTitle(title);
        return Page.fromUnpaginated(unpaginatedDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<Document> searchPaginatedByAuthor(@NonNull String author, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocuments = this.searchByAuthor(author);
        return Page.fromUnpaginated(unpaginatedDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<Document> searchPaginatedByGenre(@NonNull String genre, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocuments = this.searchByGenre(genre);
        return Page.fromUnpaginated(unpaginatedDocuments, pageIndex, maxPageSize);
    }
}
