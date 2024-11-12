package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class InMemoryLinearDocumentSearchGenerator implements DocumentSearchGenerator {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByTitle(@NonNull String title) {
        return this.searchDocumentsByPredicate(matchesTitle(title));
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByAuthor(@NonNull String author) {
        return this.searchDocumentsByPredicate(matchesAuthor(author));
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByGenre(@NonNull String genre) {
        return this.searchDocumentsByPredicate(matchesGenre(genre));
    }

    private @NonNull List<FragmentaryDocument> searchDocumentsByPredicate(@NonNull Predicate<? super FragmentaryDocument> predicate) {
        val fragmentaryDocuments = this.documentRepository.showFragmentaryDocuments();

        return fragmentaryDocuments.parallelStream()
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
        return sequences.parallelStream()
            .anyMatch(sequence -> isSubsequence(subsequence, sequence));
    }
}
