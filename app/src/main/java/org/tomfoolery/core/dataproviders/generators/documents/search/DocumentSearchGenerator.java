package org.tomfoolery.core.dataproviders.generators.documents.search;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.utils.dataclasses.common.Page;

import java.util.List;

public interface DocumentSearchGenerator extends BaseSynchronizedGenerator<Document, Document.Id> {
    @NonNull List<FragmentaryDocument> searchDocumentsByTitlePrefix(@NonNull String title);
    @NonNull List<FragmentaryDocument> searchDocumentsByTitleSuffix(@NonNull String title);
    @NonNull List<FragmentaryDocument> searchDocumentsByTitleSubsequence(@NonNull String title);

    @NonNull List<FragmentaryDocument> searchDocumentsByAuthorPrefix(@NonNull String author);
    @NonNull List<FragmentaryDocument> searchDocumentsByAuthorSuffix(@NonNull String author);
    @NonNull List<FragmentaryDocument> searchDocumentsByAuthorSubsequence(@NonNull String author);

    @NonNull List<FragmentaryDocument> searchDocumentsByGenrePrefix(@NonNull String genre);
    @NonNull List<FragmentaryDocument> searchDocumentsByGenreSuffix(@NonNull String genre);
    @NonNull List<FragmentaryDocument> searchDocumentsByGenreSubsequence(@NonNull String genre);

    default @Nullable Page<FragmentaryDocument> searchPaginatedDocumentsByTitlePrefix(@NonNull String title, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedFragmentaryDocuments = this.searchDocumentsByTitlePrefix(title);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<FragmentaryDocument> searchPaginatedDocumentsByTitleSuffix(@NonNull String title, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedFragmentaryDocuments = this.searchDocumentsByTitleSuffix(title);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<FragmentaryDocument> searchPaginatedDocumentsByTitleSubsequence(@NonNull String title, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedFragmentaryDocuments = this.searchDocumentsByTitleSubsequence(title);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<FragmentaryDocument> searchPaginatedDocumentsByAuthorPrefix(@NonNull String author, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedFragmentaryDocuments = this.searchDocumentsByAuthorPrefix(author);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<FragmentaryDocument> searchPaginatedDocumentsByAuthorSuffix(@NonNull String author, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedFragmentaryDocuments = this.searchDocumentsByAuthorSuffix(author);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<FragmentaryDocument> searchPaginatedDocumentsByAuthorSubsequence(@NonNull String author, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedFragmentaryDocuments = this.searchDocumentsByAuthorSubsequence(author);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<FragmentaryDocument> searchPaginatedDocumentsByGenrePrefix(@NonNull String genre, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedFragmentaryDocuments = this.searchDocumentsByGenrePrefix(genre);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<FragmentaryDocument> searchPaginatedDocumentsByGenreSuffix(@NonNull String genre, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedFragmentaryDocuments = this.searchDocumentsByGenreSuffix(genre);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<FragmentaryDocument> searchPaginatedDocumentsByGenreSubsequence(@NonNull String genre, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedFragmentaryDocuments = this.searchDocumentsByGenreSubsequence(genre);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }
}
