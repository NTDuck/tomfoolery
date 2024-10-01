package org.tomfoolery.core.usecases.staff;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.utils.function.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class UpdateDictionaryEntryUseCase implements ThrowableFunction<UpdateDictionaryEntryUseCase.Request, UpdateDictionaryEntryUseCase.Response> {
    private final @NonNull DocumentRepository documents;

    @Override
    public Response apply(@NonNull Request request) throws DocumentNotFoundException {
        val document = request.getDocument();
        val documentId = document.getId();

        if (!this.documents.contains(documentId))
            throw new DocumentNotFoundException();

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

    public static class DocumentNotFoundException extends Exception {}
}
