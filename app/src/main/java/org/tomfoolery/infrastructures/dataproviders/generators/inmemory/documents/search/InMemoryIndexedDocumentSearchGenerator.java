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
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.abc.BaseInMemorySynchronizedGenerator;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@NoArgsConstructor(staticName = "of")
public class InMemoryIndexedDocumentSearchGenerator extends BaseInMemorySynchronizedGenerator<Document, Document.Id> implements DocumentSearchGenerator {
    private final @NonNull DAWGSet documentTitles = new ModifiableDAWGSet();
    private final @NonNull DAWGSet documentAuthors = new ModifiableDAWGSet();
    private final @NonNull DAWGSet documentGenres = new ModifiableDAWGSet();

    private final @NonNull Multimap<String, FragmentaryDocument> documentsByTitles = Multimaps.synchronizedMultimap(ArrayListMultimap.create());
    private final @NonNull Multimap<String, FragmentaryDocument> documentsByAuthors = Multimaps.synchronizedMultimap(ArrayListMultimap.create());
    private final @NonNull Multimap<String, FragmentaryDocument> documentsByGenres = Multimaps.synchronizedMultimap(ArrayListMultimap.create());

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByTitlePrefix(@NonNull String title) {
        return searchDocuments(title, this.documentTitles::getStringsStartingWith, this.documentsByTitles);
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByTitleSuffix(@NonNull String title) {
        return searchDocuments(title, this.documentTitles::getStringsEndingWith, this.documentsByTitles);
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByTitleSubsequence(@NonNull String title) {
        return searchDocuments(title, this.documentTitles::getStringsWithSubstring, this.documentsByTitles);
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByAuthorPrefix(@NonNull String author) {
        return searchDocuments(author, this.documentAuthors::getStringsStartingWith, this.documentsByAuthors);
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByAuthorSuffix(@NonNull String author) {
        return searchDocuments(author, this.documentAuthors::getStringsEndingWith, this.documentsByAuthors);
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByAuthorSubsequence(@NonNull String author) {
        return searchDocuments(author, this.documentAuthors::getStringsWithSubstring, this.documentsByAuthors);
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByGenrePrefix(@NonNull String genre) {
        return searchDocuments(genre, this.documentGenres::getStringsStartingWith, this.documentsByGenres);
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByGenreSuffix(@NonNull String genre) {
        return searchDocuments(genre, this.documentGenres::getStringsEndingWith, this.documentsByGenres);
    }

    @Override
    public @NonNull List<FragmentaryDocument> searchDocumentsByGenreSubsequence(@NonNull String genre) {
        return searchDocuments(genre, this.documentGenres::getStringsWithSubstring, this.documentsByGenres);
    }

    @Override
    public void synchronizeRecentlySavedEntities(@NonNull Set<Document> savedEntities) {
        savedEntities.parallelStream()
            .map(FragmentaryDocument::of)
            .forEach(this::synchronizeRecentlySavedEntity);
    }

    @Override
    public void synchronizeRecentlyDeletedEntities(@NonNull Set<Document> deletedEntities) {
        deletedEntities.parallelStream()
            .map(FragmentaryDocument::of)
            .forEach(this::synchronizeRecentlyDeletedEntity);
    }

    private static @NonNull List<FragmentaryDocument> searchDocuments(@NonNull String searchTerm, @NonNull Function<String, Iterable<String>> searchFunction, @NonNull Multimap<String, FragmentaryDocument> multimap) {
        return StreamSupport.stream(searchFunction.apply(searchTerm).spliterator(), false)
            .flatMap(result -> multimap.get(result).stream())
            .collect(Collectors.toUnmodifiableList());
    }

    @Locked
    private void synchronizeRecentlySavedEntity(@NonNull FragmentaryDocument fragmentaryDocument) {
        val documentMetadata = fragmentaryDocument.getMetadata();

        val documentTitle = documentMetadata.getTitle();
        val documentAuthors = documentMetadata.getAuthors();
        val documentGenres = documentMetadata.getGenres();

        this.documentTitles.add(documentTitle);
        this.documentAuthors.addAll(documentAuthors);
        this.documentGenres.addAll(documentGenres);

        this.documentsByTitles.put(documentTitle, fragmentaryDocument);
        documentAuthors.forEach(documentAuthor -> this.documentsByAuthors.put(documentAuthor, fragmentaryDocument));
        documentGenres.forEach(documentGenre -> this.documentsByGenres.put(documentGenre, fragmentaryDocument));
    }

    @Locked
    private void synchronizeRecentlyDeletedEntity(@NonNull FragmentaryDocument fragmentaryDocument) {
        val documentMetadata = fragmentaryDocument.getMetadata();

        val documentTitle = documentMetadata.getTitle();
        val documentAuthors = documentMetadata.getAuthors();
        val documentGenres = documentMetadata.getGenres();

        this.documentsByTitles.remove(documentTitle, fragmentaryDocument);
        documentAuthors.forEach(documentAuthor -> this.documentsByAuthors.remove(documentAuthor, fragmentaryDocument));
        documentGenres.forEach(documentGenre -> this.documentsByGenres.remove(documentGenre, fragmentaryDocument));

        // if (!this.documentsByTitles.containsKey(documentTitle))
        //     this.documentTitles.remove(documentTitle);
        //
        // documentAuthors.forEach(documentAuthor -> {
        //     if (!this.documentsByAuthors.containsKey(documentAuthor))
        //         this.documentAuthors.remove(documentAuthor);
        // });
        //
        // documentGenres.forEach(documentGenre -> {
        //     if (!this.documentsByGenres.containsKey(documentGenre))
        //         this.documentGenres.remove(documentGenre);
        // });
    }
}
