package org.tomfoolery.core.usecases.external.patron.browse;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.Patron;
import org.tomfoolery.core.utils.function.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class BorrowDocumentUseCase implements ThrowableFunction<BorrowDocumentUseCase.Request, BorrowDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public @NonNull Response apply(@NonNull Request request) throws DocumentNotFoundException, DocumentAlreadyBorrowedException {
        val patron = request.getPatron();
        val documentId = request.getDocumentId();

        val document = this.documentRepository.getById(documentId);
        if (document == null)
            throw new DocumentNotFoundException();

        if (isDocumentAlreadyBorrowed(patron, documentId))
            throw new DocumentAlreadyBorrowedException();

        markDocumentAsBorrowed(patron, document);

        return Response.of(document);
    }

    private static boolean isDocumentAlreadyBorrowed(@NonNull Patron patron, Document.@NonNull Id documentId) {
        val patronAudit = patron.getAudit();
        val borrowedDocumentIdsOfPatron = patronAudit.getBorrowedDocumentIds();
        return borrowedDocumentIdsOfPatron.contains(documentId);
    }

    private static void markDocumentAsBorrowed(@NonNull Patron patron, @NonNull Document document) {
        val patronId = patron.getId();
        val documentId = document.getId();

        val patronAudit = patron.getAudit();
        val borrowedDocumentIdsOfPatron = patronAudit.getBorrowedDocumentIds();
        borrowedDocumentIdsOfPatron.add(documentId);

        val documentAudit = document.getAudit();
        val borrowingPatronIdsOfDocument = documentAudit.getBorrowingPatronIds();
        borrowingPatronIdsOfDocument.add(patronId);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull Patron patron;
        Document.@NonNull Id documentId;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Document document;
    }

    public static class DocumentNotFoundException extends Exception {}
    public static class DocumentAlreadyBorrowedException extends Exception {}
}
