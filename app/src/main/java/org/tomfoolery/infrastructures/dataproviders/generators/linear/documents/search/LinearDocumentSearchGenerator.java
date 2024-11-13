package org.tomfoolery.infrastructures.dataproviders.generators.linear.documents.search;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class LinearDocumentSearchGenerator implements DocumentSearchGenerator {
    private final @NonNull Repository repository;

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByTitlePrefix(@NonNull String title) {
        return this.searchDocumentsByPredicate(document -> matchesPrefix(title, document.getMetadata().getTitle()));
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByTitleSuffix(@NonNull String title) {
        return this.searchDocumentsByPredicate(document -> matchesSuffix(title, document.getMetadata().getTitle()));
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByTitleSubsequence(@NonNull String title) {
        return this.searchDocumentsByPredicate(document -> isSubsequence(title, document.getMetadata().getTitle()));
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByAuthorPrefix(@NonNull String author) {
        return this.searchDocumentsByPredicate(document -> matchesPrefix(author, document.getMetadata().getAuthors()));
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByAuthorSuffix(@NonNull String author) {
        return this.searchDocumentsByPredicate(document -> matchesSuffix(author, document.getMetadata().getAuthors()));
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByAuthorSubsequence(@NonNull String author) {
        return this.searchDocumentsByPredicate(document -> isSubsequence(author, document.getMetadata().getAuthors()));
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByGenrePrefix(@NonNull String genre) {
        return this.searchDocumentsByPredicate(document -> matchesPrefix(genre, document.getMetadata().getGenres()));
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByGenreSuffix(@NonNull String genre) {
        return this.searchDocumentsByPredicate(document -> matchesSuffix(genre, document.getMetadata().getGenres()));
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByGenreSubsequence(@NonNull String genre) {
        return this.searchDocumentsByPredicate(document -> isSubsequence(genre, document.getMetadata().getGenres()));
    }

    @Override
    public void synchronizeRecentlySavedEntities(@NonNull Set<Document> savedEntities) {
        savedEntities.parallelStream()
            .map(FragmentaryDocument::of)
            .forEach(this.repository::saveDocument);
    }

    @Override
    public void synchronizeRecentlyDeletedEntities(@NonNull Set<Document.Id> deletedEntities) {
        deletedEntities.parallelStream()
            .forEach(this.repository::deleteDocument);
    }

    @Override
    public @NonNull Instant getLastSynchronizedTimestamp() {
        return this.repository.getLastSynchronizedTimestamp();
    }

    @Override
    public void setLastSynchronizedTimestamp(@NonNull Instant lastSynchronizedTimestamp) {
        this.repository.setLastSynchronizedTimestamp(lastSynchronizedTimestamp);
    }

    private @NonNull List<FragmentaryDocument> searchDocumentsByPredicate(@NonNull Predicate<? super FragmentaryDocument> predicate) {
        return this.repository.showDocuments().parallelStream()
            .filter(predicate)
            .collect(Collectors.toUnmodifiableList());
    }

    private static boolean matchesPrefix(@NonNull String prefix, @NonNull String target) {
        return target.startsWith(prefix);
    }

    private static boolean matchesPrefix(@NonNull String prefix, @NonNull Collection<String> targets) {
        return targets.parallelStream()
            .anyMatch(target -> matchesPrefix(prefix, target));
    }

    private static boolean matchesSuffix(@NonNull String suffix, @NonNull String target) {
        return target.endsWith(suffix);
    }

    private static boolean matchesSuffix(@NonNull String suffix, @NonNull Collection<String> targets) {
        return targets.parallelStream()
            .anyMatch(target -> matchesSuffix(suffix, target));
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

    public interface Repository {
        @NonNull Collection<FragmentaryDocument> showDocuments();

        void saveDocument(@NonNull FragmentaryDocument fragmentaryDocument);
        void deleteDocument(Document.@NonNull Id documentId);

        @NonNull Instant getLastSynchronizedTimestamp();
        void setLastSynchronizedTimestamp(@NonNull Instant lastSynchronizedTimestamp);
    }
}
