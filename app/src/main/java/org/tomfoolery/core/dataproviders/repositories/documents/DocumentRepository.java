package org.tomfoolery.core.dataproviders.repositories.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface DocumentRepository extends BaseRepository<Document, Document.Id> {
    default void save(@NonNull FragmentaryDocument fragmentaryDocument) {
        val documentId = fragmentaryDocument.getId();
        val document = this.getById(documentId);

        if (document == null)
            return;

        document.setMetadata(fragmentaryDocument.getMetadata());
        document.setAudit(fragmentaryDocument.getAudit());

        this.save(document);
    }

    default @Nullable FragmentaryDocument getFragmentaryById(Document.@NonNull Id documentId) {
        val document = this.getById(documentId);

        if (document == null)
            return null;

        return FragmentaryDocument.of(document);
    }

    default @NonNull Collection<FragmentaryDocument> showFragmentary() {
        val documents = this.show();

        return documents.stream()
            .map(FragmentaryDocument::of)
            .collect(Collectors.toUnmodifiableList());
    }

    default @Nullable Page<FragmentaryDocument> showPaginatedFragmentary(int pageIndex, int maxPageSize) {
        val unpaginatedFragmentaryDocuments = this.showFragmentary();
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }

    default @NonNull Collection<FragmentaryDocument> searchFragmentaryDocumentsByTitle(@NonNull String title) {
        return this.searchFragmentaryDocumentsByPredicate(matchesTitle(title));
    }

    default @NonNull Collection<FragmentaryDocument> searchFragmentaryDocumentsByAuthor(@NonNull String author) {
        return this.searchFragmentaryDocumentsByPredicate(matchesAuthor(author));
    }

    default @NonNull Collection<FragmentaryDocument> searchFragmentaryDocumentsByGenre(@NonNull String genre) {
        return this.searchFragmentaryDocumentsByPredicate(matchesGenre(genre));
    }

    default @Nullable Page<FragmentaryDocument> searchPaginatedFragmentaryDocumentsByTitle(@NonNull String title, int pageIndex, int maxPageSize) {
        val unpaginatedFragmentaryDocuments = searchFragmentaryDocumentsByTitle(title);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<FragmentaryDocument> searchPaginatedFragmentaryDocumentsByAuthor(@NonNull String author, int pageIndex, int maxPageSize) {
        val unpaginatedFragmentaryDocuments = searchFragmentaryDocumentsByAuthor(author);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }

    default @Nullable Page<FragmentaryDocument> searchPaginatedFragmentaryDocumentsByGenre(@NonNull String genre, int pageIndex, int maxPageSize) {
        val unpaginatedFragmentaryDocuments = searchFragmentaryDocumentsByGenre(genre);
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }

    private @NonNull Collection<FragmentaryDocument> searchFragmentaryDocumentsByPredicate(@NonNull Predicate<? super FragmentaryDocument> predicate) {
        val fragmentaryDocuments = this.showFragmentary();

        return fragmentaryDocuments.stream()
            .filter(predicate)
            .collect(Collectors.toUnmodifiableList());
    }

    private static @NonNull Predicate<FragmentaryDocument> matchesTitle(@NonNull String title) {
        return fragmentaryDocument -> isSubsequence(title, fragmentaryDocument.getMetadata().getTitle());
    }

    private static @NonNull Predicate<FragmentaryDocument> matchesAuthor(@NonNull String author) {
        return fragmentaryDocument -> isSubsequence(author, fragmentaryDocument.getMetadata().getAuthors());
    }

    private static @NonNull Predicate<FragmentaryDocument> matchesGenre(@NonNull String genre) {
        return fragmentaryDocument -> isSubsequence(genre, fragmentaryDocument.getMetadata().getGenres());
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
