package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.Locked;
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
    private final @NonNull DAWGSet normalizedTitles = new ModifiableDAWGSet();
    private final @NonNull DAWGSet normalizedAuthors = new ModifiableDAWGSet();
    private final @NonNull DAWGSet normalizedGenres = new ModifiableDAWGSet();

    private final @NonNull Multimap<String, Document> documentsByNormalizedTitles = Multimaps.synchronizedMultimap(ArrayListMultimap.create());
    private final @NonNull Multimap<String, Document> documentsByNormalizedAuthors = Multimaps.synchronizedMultimap(ArrayListMultimap.create());
    private final @NonNull Multimap<String, Document> documentsByNormalizedGenres = Multimaps.synchronizedMultimap(ArrayListMultimap.create());

    @Override
    @Locked.Read
    public @NonNull List<Document> searchByNormalizedTitle(@NonNull String title) {
        return searchDocuments(title, this.normalizedTitles::getStringsWithSubstring, this.documentsByNormalizedTitles);
    }

    @Override
    @Locked.Read
    public @NonNull List<Document> searchByNormalizedAuthor(@NonNull String author) {
        return searchDocuments(author, this.normalizedAuthors::getStringsWithSubstring, this.documentsByNormalizedAuthors);
    }

    @Override
    @Locked.Read
    public @NonNull List<Document> searchByNormalizedGenre(@NonNull String genre) {
        return searchDocuments(genre, this.normalizedGenres::getStringsWithSubstring, this.documentsByNormalizedGenres);
    }

    @Override
    @Locked.Write
    public void synchronizeSavedEntity(@NonNull Document savedDocument) {
        val documentMetadata = savedDocument.getMetadata();

        val normalizedTitle = this.normalize(documentMetadata.getTitle());
        val normalizedAuthors = this.normalize(documentMetadata.getAuthors());
        val normalizedGenres = this.normalize(documentMetadata.getGenres());

        this.normalizedTitles.add(normalizedTitle);
        this.normalizedAuthors.addAll(normalizedAuthors);
        this.normalizedGenres.addAll(normalizedGenres);

        this.documentsByNormalizedTitles.put(normalizedTitle, savedDocument);
        normalizedAuthors.forEach(normalizedAuthor -> this.documentsByNormalizedAuthors.put(normalizedAuthor, savedDocument));
        normalizedGenres.forEach(normalizedGenre -> this.documentsByNormalizedGenres.put(normalizedGenre, savedDocument));
    }

    @Override
    @Locked.Write
    public void synchronizeDeletedEntity(@NonNull Document deletedDocument) {
        val documentMetadata = deletedDocument.getMetadata();

        val normalizedTitle = this.normalize(documentMetadata.getTitle());
        val normalizedAuthors = this.normalize(documentMetadata.getAuthors());
        val normalizedGenres = this.normalize(documentMetadata.getGenres());

        this.documentsByNormalizedTitles.remove(normalizedTitle, deletedDocument);
        normalizedAuthors.forEach(normalizedAuthor -> this.documentsByNormalizedAuthors.remove(normalizedAuthor, deletedDocument));
        normalizedGenres.forEach(normalizedGenre -> this.documentsByNormalizedGenres.remove(normalizedGenre, deletedDocument));
    }

    private static @NonNull List<Document> searchDocuments(@NonNull String searchTerm, @NonNull Function<String, Iterable<String>> searchFunction, @NonNull Multimap<String, Document> multimap) {
        return StreamSupport.stream(searchFunction.apply(searchTerm).spliterator(), false)
            .flatMap(result -> multimap.get(result).stream())
            .collect(Collectors.toUnmodifiableList());
    }
}
