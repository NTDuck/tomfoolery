package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.quinto.dawg.DAWGMap;
import org.quinto.dawg.ModifiableDAWGMap;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;

import java.util.List;

public class InMemoryIndexedDocumentSearchGenerator implements DocumentSearchGenerator {
    private final @NonNull DAWGMap documentsByTitles = new ModifiableDAWGMap();

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
