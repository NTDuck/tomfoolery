package org.tomfoolery.infrastructures.dataproviders.generators.apache.lucene.documents.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public class InMemoryApacheLuceneDocumentSearchGenerator implements DocumentSearchGenerator {
    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByTitlePrefix(@NonNull String title) {
        return List.of();
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByTitleSuffix(@NonNull String title) {
        return List.of();
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByTitleSubsequence(@NonNull String title) {
        return List.of();
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByAuthorPrefix(@NonNull String author) {
        return List.of();
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByAuthorSuffix(@NonNull String author) {
        return List.of();
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByAuthorSubsequence(@NonNull String author) {
        return List.of();
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByGenrePrefix(@NonNull String genre) {
        return List.of();
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByGenreSuffix(@NonNull String genre) {
        return List.of();
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByGenreSubsequence(@NonNull String genre) {
        return List.of();
    }

    @Override
    public void synchronizeRecentlySavedEntities(@NonNull Set<Document> savedEntities) {

    }

    @Override
    public void synchronizeRecentlyDeletedEntities(@NonNull Set<Document> deletedEntities) {

    }

    @Override
    public @NonNull Instant getLastSynchronizedTimestamp() {
        return null;
    }

    @Override
    public void setLastSynchronizedTimestamp(@NonNull Instant lastSynchronizedTimestamp) {

    }
}
