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
    public @NonNull List<Document> searchByNormalizedTitle(@NonNull String normalizedTitle) {
        return this.searchDocumentsByPredicate(document ->
            isSubsequence(normalizedTitle, this.normalize(document.getMetadata().getTitle())));
    }

    @Override
    public @NonNull List<Document> searchByNormalizedAuthor(@NonNull String normalizedAuthor) {
        return this.searchDocumentsByPredicate(document ->
            isSubsequence(normalizedAuthor, this.normalize(document.getMetadata().getAuthors())));
    }

    @Override
    public @NonNull List<Document> searchByNormalizedGenre(@NonNull String normalizedGenre) {
        return this.searchDocumentsByPredicate(document ->
            isSubsequence(normalizedGenre, this.normalize(document.getMetadata().getGenres())));
    }

    private @NonNull List<Document> searchDocumentsByPredicate(@NonNull Predicate<? super Document> predicate) {
        return super.cachedEntities.parallelStream()
            .filter(predicate)
            .collect(Collectors.toUnmodifiableList());
    }
}
