package org.tomfoolery.core.usecases;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;

import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.utils.function.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class AddDocumentUseCase implements ThrowableFunction<AddDocumentUseCase.Request, AddDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documents;

    @Override
    public Response apply(@NonNull Request request) throws DocumentAlreadyExistsException {
        val document = request.getDocument();
        val documentId = document.getId();

        if (this.documents.contains(documentId))
            throw new DocumentAlreadyExistsException();

        this.documents.save(document);

        return Response.of(document);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull Document document;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Document document;
    }

    public static class DocumentAlreadyExistsException extends Exception {}
}