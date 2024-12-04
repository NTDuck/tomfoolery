package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.quinto.dawg.DAWGSet;
import org.quinto.dawg.ModifiableDAWGSet;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.domain.documents.Document;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@NoArgsConstructor(staticName = "of")
public class InMemoryIndexedDocumentSearchGenerator implements DocumentSearchGenerator {
    private final @NonNull DAWGSet documentTitles = new ModifiableDAWGSet();
    private final @NonNull DAWGSet documentAuthors = new ModifiableDAWGSet();
    private final @NonNull DAWGSet documentGenres = new ModifiableDAWGSet();

    private final @NonNull Multimap<String, Document> documentsByTitles = Multimaps.synchronizedMultimap(ArrayListMultimap.create());
    private final @NonNull Multimap<String, Document> documentsByAuthors = Multimaps.synchronizedMultimap(ArrayListMultimap.create());
    private final @NonNull Multimap<String, Document> documentsByGenres = Multimaps.synchronizedMultimap(ArrayListMultimap.create());

    @Override
    public @NonNull List<Document> searchByTitle(@NonNull String title) {
        return searchDocuments(title, this.documentTitles::getStringsWithSubstring, this.documentsByTitles);
    }

    @Override
    public @NonNull List<Document> searchByAuthor(@NonNull String author) {
        return searchDocuments(author, this.documentAuthors::getStringsWithSubstring, this.documentsByAuthors);
    }

    @Override
    public @NonNull List<Document> searchByGenre(@NonNull String genre) {
        return searchDocuments(genre, this.documentGenres::getStringsWithSubstring, this.documentsByGenres);
    }

    @Override
    public void synchronizeSavedEntity(@NonNull Document savedDocument) {
        val documentMetadata = savedDocument.getMetadata();

        val documentTitle = documentMetadata.getTitle();
        val documentAuthors = documentMetadata.getAuthors();
        val documentGenres = documentMetadata.getGenres();

        this.documentTitles.add(documentTitle);
        this.documentAuthors.addAll(documentAuthors);
        this.documentGenres.addAll(documentGenres);

        this.documentsByTitles.put(documentTitle, savedDocument);
        documentAuthors.forEach(documentAuthor -> this.documentsByAuthors.put(documentAuthor, savedDocument));
        documentGenres.forEach(documentGenre -> this.documentsByGenres.put(documentGenre, savedDocument));
    }

    @Override
    public void synchronizeDeletedEntity(@NonNull Document deletedDocument) {
        val documentMetadata = deletedDocument.getMetadata();

        val documentTitle = documentMetadata.getTitle();
        val documentAuthors = documentMetadata.getAuthors();
        val documentGenres = documentMetadata.getGenres();

        this.documentsByTitles.remove(documentTitle, deletedDocument);
        documentAuthors.forEach(documentAuthor -> this.documentsByAuthors.remove(documentAuthor, deletedDocument));
        documentGenres.forEach(documentGenre -> this.documentsByGenres.remove(documentGenre, deletedDocument));
    }

    private static @NonNull List<Document> searchDocuments(@NonNull String searchTerm, @NonNull Function<String, Iterable<String>> searchFunction, @NonNull Multimap<String, Document> multimap) {
        return StreamSupport.stream(searchFunction.apply(searchTerm).spliterator(), false)
            .flatMap(result -> multimap.get(result).stream())
            .collect(Collectors.toUnmodifiableList());
    }
}
