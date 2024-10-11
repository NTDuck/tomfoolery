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
public class SearchDocumentsByGenresUseCase implements Function<SearchDocumentsByGenresUseCase.Request, SearchDocumentsByGenresUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public @NonNull Response apply(@NonNull Request request) {
        val genres = request.getGenres();
        val documents = this.documentRepository.searchByGenres(genres);
        return Response.of(documents);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull Collection<String> genres;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull
        Collection<Document> documents;
    }
}
