package org.tomfoolery.core.usecases.interactive.staff;

import lombok.*;

import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.utils.function.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class UpdateDocumentUseCase implements ThrowableFunction<UpdateDocumentUseCase.Request, UpdateDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public Response apply(@NonNull Request request) throws Exception {
        val documentId = request.getDocumentId();
        val documentMetadata = request.getDocumentMetadata();

        val document = this.documentRepository.getById(documentId);
        if (document == null)
            throw new DocumentNotFoundException();

        document.setMetadata(documentMetadata);
        this.documentRepository.save(document);

        return Response.of(documentId);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull Document.ID documentId;
        @NonNull Document.Metadata documentMetadata;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Document.ID documentId;
    }

    public static class DocumentNotFoundException extends Exception {}
}
