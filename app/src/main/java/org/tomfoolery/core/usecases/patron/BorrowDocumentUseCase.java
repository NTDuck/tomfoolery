package org.tomfoolery.core.usecases.patron;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.Patron;
import org.tomfoolery.core.utils.function.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class BorrowDocumentUseCase implements ThrowableFunction<BorrowDocumentUseCase.Request, BorrowDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documents;

    @Override
    public Response apply(Request request) throws DocumentAlreadyBorrowedException, DocumentNotFoundException {
        val documentId = request.getDocumentId();
        val patron = request.getPatron();
        val borrowedDocumentIds = patron.getBorrowedDocumentIds();

        if (borrowedDocumentIds.contains(documentId))
            throw new DocumentAlreadyBorrowedException();

        if (!this.documents.contains(documentId))
            throw new DocumentNotFoundException();

        val document = this.documents.get(documentId);
        val borrowingPatronIds = document.getBorrowingPatronIds();
        val patronId = patron.getId();

        borrowedDocumentIds.add(documentId);
        borrowingPatronIds.add(patronId);

        return Response.of(document);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull Document.ID documentId;
        @NonNull Patron patron;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Document document;
    }

    public static class DocumentAlreadyBorrowedException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
}
