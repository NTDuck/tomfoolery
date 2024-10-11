package org.tomfoolery.core.usecases.external.staff;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.utils.function.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class UpdateDocumentUseCase implements ThrowableFunction<UpdateDocumentUseCase.Request, UpdateDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public @NonNull Response apply(@NonNull Request request) throws Exception {
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
        Document.@NonNull Id documentId;
        Document.@NonNull Metadata documentMetadata;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        Document.@NonNull Id documentId;
    }

    public static class DocumentNotFoundException extends Exception {}
}
