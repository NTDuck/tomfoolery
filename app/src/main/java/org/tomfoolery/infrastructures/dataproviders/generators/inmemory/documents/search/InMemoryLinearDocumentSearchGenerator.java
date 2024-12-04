package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.abc.InMemoryLinearDocumentGenerator;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class InMemoryLinearDocumentSearchGenerator extends InMemoryLinearDocumentGenerator implements DocumentSearchGenerator {
    @Override
    public @NonNull List<Document> searchByTitle(@NonNull String title) {
        return this.searchDocumentsByPredicate(document -> isSubsequence(title, document.getMetadata().getTitle()));
    }

    @Override
    public @NonNull List<Document> searchByAuthor(@NonNull String author) {
        return this.searchDocumentsByPredicate(document -> isSubsequence(author, document.getMetadata().getAuthors()));
    }

    @Override
    public @NonNull List<Document> searchByGenre(@NonNull String genre) {
        return this.searchDocumentsByPredicate(document -> isSubsequence(genre, document.getMetadata().getGenres()));
    }

    private @NonNull List<Document> searchDocumentsByPredicate(@NonNull Predicate<? super Document> predicate) {
        return super.cachedEntities.parallelStream()
            .filter(predicate)
            .collect(Collectors.toUnmodifiableList());
    }
}
