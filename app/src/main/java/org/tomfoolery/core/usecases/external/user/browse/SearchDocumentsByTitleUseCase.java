package org.tomfoolery.core.usecases.interactive.user;

import lombok.*;

import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;

import java.util.Collection;
import java.util.function.Function;

@RequiredArgsConstructor(staticName = "of")
public class SearchDocumentsByTitleUseCase implements Function<SearchDocumentsByTitleUseCase.Request, SearchDocumentsByTitleUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public Response apply(@NonNull Request request) {
        val title = request.getTitle();
        val documents = this.documentRepository.searchByTitle(title);
        return Response.of(documents);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String title;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull
        Collection<Document> documents;
    }
}
