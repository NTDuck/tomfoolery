package org.tomfoolery.core.usecases;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.utils.function.ThrowableConsumer;

@RequiredArgsConstructor(staticName = "of")
public class DeleteDocumentUseCase implements ThrowableConsumer<DeleteDocumentUseCase.Request> {
    private final @NonNull DocumentRepository documents;

    @Override
    public void accept(@NonNull Request request) throws DocumentNotFoundException {
        val documentId = request.getDocumentId();

        if (!this.documents.contains(documentId))
            throw new DocumentNotFoundException();

        this.documents.delete(documentId);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull Document.ID documentId;
    }

    public static class DocumentNotFoundException extends Exception {}
}
