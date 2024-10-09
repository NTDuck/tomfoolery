package org.tomfoolery.core.usecases.interactive.patron;

import lombok.*;

import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.utils.function.ThrowableConsumer;

@RequiredArgsConstructor(staticName = "of")
public class RemoveDocumentUseCase implements ThrowableConsumer<RemoveDocumentUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public void accept(@NonNull Request request) throws DocumentNotFoundException {
        val documentId = request.getDocumentId();

        if (!this.documentRepository.contains(documentId))
            throw new DocumentNotFoundException();

        this.documentRepository.delete(documentId);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull Document.ID documentId;
    }

    public static class DocumentNotFoundException extends Exception {}
}
