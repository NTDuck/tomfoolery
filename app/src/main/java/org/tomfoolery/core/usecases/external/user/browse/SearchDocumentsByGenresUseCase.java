package org.tomfoolery.core.usecases.interactive.user;

import lombok.*;

import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;

import java.util.Collection;
import java.util.function.Function;

@RequiredArgsConstructor(staticName = "of")
public class SearchDocumentsByGenresUseCase implements Function<SearchDocumentsByGenresUseCase.Request, SearchDocumentsByGenresUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public Response apply(@NonNull Request request) {
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
