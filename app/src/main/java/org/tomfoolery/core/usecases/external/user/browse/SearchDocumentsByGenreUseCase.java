package org.tomfoolery.core.usecases.external.user.browse;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;

import java.util.Collection;
import java.util.function.Function;

@RequiredArgsConstructor(staticName = "of")
public class SearchDocumentsByGenreUseCase implements Function<SearchDocumentsByGenreUseCase.Request, SearchDocumentsByGenreUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public @NonNull Response apply(@NonNull Request request) {
        val genre = request.getGenre();
        val documents = this.documentRepository.searchByGenre(genre);
        return Response.of(documents);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String genre;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Collection<Document> documents;
    }
}
