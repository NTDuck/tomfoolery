package org.tomfoolery.core.dataproviders.generators.documents.search;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.utils.dataclasses.common.Page;

import java.util.List;

public interface DocumentSearchGenerator {
    @NonNull List<FragmentaryDocument> searchDocumentsByTitle(@NonNull String title);
    @NonNull List<FragmentaryDocument> searchDocumentsByAuthor(@NonNull String author);
    @NonNull List<FragmentaryDocument> searchDocumentsByGenre(@NonNull String genre);

    default @Nullable Page<FragmentaryDocument> searchPaginatedDocumentsByTitle(@NonNull String title, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedFragmentaryDocuments = searchDocumentsByTitle(title);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<FragmentaryDocument> searchPaginatedDocumentsByAuthor(@NonNull String author, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedFragmentaryDocuments = searchDocumentsByAuthor(author);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<FragmentaryDocument> searchPaginatedDocumentsByGenre(@NonNull String genre, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedFragmentaryDocuments = searchDocumentsByGenre(genre);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }
}
