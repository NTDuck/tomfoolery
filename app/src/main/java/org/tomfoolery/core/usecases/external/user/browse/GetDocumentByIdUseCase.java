package org.tomfoolery.core.usecases.external.user.browse;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.utils.functional.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class GetDocumentByIdUseCase implements ThrowableFunction<GetDocumentByIdUseCase.Request, GetDocumentByIdUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public @NonNull Response apply(@NonNull Request request) throws Exception {
        val documentId = request.getDocumentId();

        val document = this.documentRepository.getById(documentId);
        if (document == null)
            throw new DocumentNotFoundException();

        return Response.of(document);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Document document;
    }

    public static class DocumentNotFoundException extends Exception {}
}
