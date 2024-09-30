package org.tomfoolery.core.usecases;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.utils.function.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class GetDocumentUseCase implements ThrowableFunction<GetDocumentUseCase.Request, GetDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documents;

    @Override
    public Response apply(@NonNull Request request) throws DocumentNotFoundException {
        val documentId = request.getDocumentId();

        if (!this.documents.contains(documentId))
            throw new DocumentNotFoundException();

        val document = this.documents.get(documentId);

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
