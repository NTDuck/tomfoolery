package org.tomfoolery.infrastructures.dataproviders.generators.apache.lucene.documents.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.domain.documents.Document;

import java.util.List;

public class InMemoryApacheLuceneDocumentSearchGenerator implements DocumentSearchGenerator {
    @Override
    public @NonNull List<Document> searchByNormalizedTitle(@NonNull String title) {
        return List.of();
    }

    @Override
    public @NonNull List<Document> searchByNormalizedAuthor(@NonNull String author) {
        return List.of();
    }

    @Override
    public @NonNull List<Document> searchByNormalizedGenre(@NonNull String genre) {
        return List.of();
    }

    @Override
    public void synchronizeSavedEntity(@NonNull Document savedEntity) {

    }

    @Override
    public void synchronizeDeletedEntity(@NonNull Document deletedEntity) {

    }
}
