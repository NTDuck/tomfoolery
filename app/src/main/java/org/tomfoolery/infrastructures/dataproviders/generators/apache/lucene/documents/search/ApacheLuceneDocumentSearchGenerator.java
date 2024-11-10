package org.tomfoolery.infrastructures.dataproviders.generators.apache.lucene.documents.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;

import java.util.List;

public class ApacheLuceneDocumentSearchGenerator implements DocumentSearchGenerator {
    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByTitle(@NonNull String title) {
        return List.of();
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByAuthor(@NonNull String author) {
        return List.of();
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByGenre(@NonNull String genre) {
        return List.of();
    }
}
