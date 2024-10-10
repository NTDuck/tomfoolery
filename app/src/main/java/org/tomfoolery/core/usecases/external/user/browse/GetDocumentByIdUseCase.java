package org.tomfoolery.core.usecases.interactive.user;

import lombok.*;

import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.utils.function.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class GetDocumentByIdUseCase implements ThrowableFunction<GetDocumentByIdUseCase.Request, GetDocumentByIdUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public Response apply(@NonNull Request request) throws Exception {
        val documentId = request.getDocumentId();

        val document = this.documentRepository.getById(documentId);
        if (document == null)
            throw new DocumentNotFoundException();

        return Response.of(document);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull Document.ID documentId;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Document document;
    }

    public static class DocumentNotFoundException extends Exception {}
}
