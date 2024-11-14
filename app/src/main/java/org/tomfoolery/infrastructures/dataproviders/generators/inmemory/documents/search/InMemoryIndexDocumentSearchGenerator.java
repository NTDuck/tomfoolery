package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.quinto.dawg.DAWGSet;
import org.quinto.dawg.ModifiableDAWGSet;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.abc.BaseInMemorySynchronizedGenerator;

import java.util.List;
import java.util.Set;

public class InMemoryIndexDocumentSearchGenerator extends BaseInMemorySynchronizedGenerator<Document, Document.Id> implements DocumentSearchGenerator {
    private final @NonNull DAWGSet fragmentaryDocumentsByTitle = new ModifiableDAWGSet();

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
        savedEntities.parallelStream()
            .map(FragmentaryDocument::of)
            .forEach(fragmentaryDocument -> );
    }

    @Override
    public void synchronizeRecentlyDeletedEntities(@NonNull Set<Document> deletedEntities) {

    }

    private void synchronizeRecentlySavedEntity(@NonNull FragmentaryDocument fragmentaryDocument) {
        val documentMetadata = fragmentaryDocument.getMetadata();

        val documentTitle = documentMetadata.getTitle();
        val documentAuthors = documentMetadata.getAuthors();
        val documentGenres = documentMetadata.getGenres();
    }
}
