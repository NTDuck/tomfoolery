package org.tomfoolery.core.usecases.interactive.user;

import lombok.*;

import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;

import java.util.Collection;
import java.util.function.Function;

@RequiredArgsConstructor(staticName = "of")
public class SearchDocumentsByAuthorUseCase implements Function<SearchDocumentsByAuthorUseCase.Request, SearchDocumentsByAuthorUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public Response apply(@NonNull Request request) {
        val author = request.getAuthor();
        val documents = this.documentRepository.searchByAuthor(author);
        return Response.of(documents);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String author;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Collection<Document> documents;
    }
}
