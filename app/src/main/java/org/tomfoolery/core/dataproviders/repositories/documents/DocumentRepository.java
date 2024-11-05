package org.tomfoolery.core.dataproviders.repositories.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.Collection;
import java.util.function.Predicate;

public interface DocumentRepository extends BaseRepository<Document, Document.Id> {
    default @NonNull Collection<Document> searchDocumentsByTitle(@NonNull String title) {
        return this.searchDocumentsByPredicate(document -> isSubsequence(title, document.getMetadata().getTitle()));
    }

    default @NonNull Collection<Document> searchDocumentsByAuthor(@NonNull String author) {
        return this.searchDocumentsByPredicate(document -> isSubsequence(author, document.getMetadata().getAuthors()));
    }

    default @NonNull Collection<Document> searchDocumentsByGenre(@NonNull String genre) {
        return this.searchDocumentsByPredicate(document -> isSubsequence(genre, document.getMetadata().getGenres()));
    }

    default @Nullable Page<Document> searchPaginatedDocumentsByTitle(@NonNull String title, int pageIndex, int maxPageSize) {
        val unpaginatedDocuments = this.searchDocumentsByTitle(title);
        return Page.fromUnpaginated(unpaginatedDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<Document> searchPaginatedDocumentsByAuthor(@NonNull String author, int pageIndex, int maxPageSize) {
        val unpaginatedDocuments = this.searchDocumentsByAuthor(author);
        return Page.fromUnpaginated(unpaginatedDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<Document> searchPaginatedDocumentsByGenre(@NonNull String genre, int pageIndex, int maxPageSize) {
        val unpaginatedDocuments = this.searchDocumentsByGenre(genre);
        return Page.fromUnpaginated(unpaginatedDocuments, pageIndex, maxPageSize);
    }

    private @NonNull Collection<Document> searchDocumentsByPredicate(@NonNull Predicate<? super Document> predicate) {
        return this.show().stream()
            .filter(predicate)
            .toList();
    }

    private static boolean isSubsequence(@NonNull CharSequence subsequence, @NonNull CharSequence sequence) {
        val lengthOfSubsequence = subsequence.length();
        val lengthOfSequence = sequence.length();

        var indexOfSubsequence = 0;
        var indexOfSequence = 0;

        while (indexOfSubsequence < lengthOfSubsequence && indexOfSequence < lengthOfSequence) {
            if (subsequence.charAt(indexOfSubsequence) == sequence.charAt(indexOfSequence))
                indexOfSubsequence++;

            indexOfSequence++;
        }

        return indexOfSubsequence == lengthOfSubsequence;
    }

    private static boolean isSubsequence(@NonNull CharSequence subsequence, @NonNull Collection<? extends CharSequence> sequences) {
        return sequences.stream()
            .anyMatch(sequence -> isSubsequence(subsequence, sequence));
    }
}
