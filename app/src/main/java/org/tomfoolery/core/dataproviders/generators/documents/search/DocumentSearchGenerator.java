package org.tomfoolery.core.dataproviders.generators.documents.search;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSearchGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.utils.dataclasses.Page;
import org.tomfoolery.core.utils.helpers.normalizers.StringNormalizer;

import java.util.List;
import java.util.stream.Collectors;

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
    //
    // @Override
    // default @NonNull Document normalize(@NonNull Document document) {
    //     val documentMetadata = document.getMetadata();
    //
    //     val documentTitle = documentMetadata.getTitle();
    //     val documentAuthors = documentMetadata.getAuthors();
    //     val documentGenres = documentMetadata.getGenres();
    //
    //     val normalizedDocumentTitle = StringNormalizer.normalize(documentTitle);
    //     val normalizedDocumentAuthors = documentAuthors.parallelStream()
    //         .map(StringNormalizer::normalize)
    //         .collect(Collectors.toUnmodifiableList());
    //     val normalizedDocumentGenres = documentGenres.parallelStream()
    //         .map(StringNormalizer::normalize)
    //         .collect(Collectors.toUnmodifiableList());
    //
    //     val normalizedDocumentMetadata = documentMetadata
    //         .withTitle(normalizedDocumentTitle)
    //         .withAuthors(normalizedDocumentAuthors)
    //         .withGenres(normalizedDocumentGenres);
    //
    //     return document.withMetadata(normalizedDocumentMetadata);
    // }
}
