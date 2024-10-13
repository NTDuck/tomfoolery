package org.tomfoolery.core.usecases.external.patron.browse;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.Patron;
import org.tomfoolery.core.utils.function.ThrowableConsumer;

@RequiredArgsConstructor(staticName = "of")
public class ReturnDocumentUseCase implements ThrowableConsumer<ReturnDocumentUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;
    
    @Override
    public void accept(@NonNull Request request) throws DocumentNotFoundException, DocumentAlreadyReturnedException {
        val patron = request.getPatron();
        val documentId = request.getDocumentId();

        val document = this.documentRepository.getById(documentId);
        if (document == null)
            throw new DocumentNotFoundException();

        if (isDocumentAlreadyReturned(patron, documentId))
            throw new DocumentAlreadyReturnedException();

        markDocumentAsReturned(patron, document);
    }

    private static boolean isDocumentAlreadyReturned(@NonNull Patron patron, Document.@NonNull Id documentId) {
        val patronAudit = patron.getAudit();
        val borrowedDocumentIdsOfPatron = patronAudit.getBorrowedDocumentIds();
        return !borrowedDocumentIdsOfPatron.contains(documentId);
    }

    private static void markDocumentAsReturned(@NonNull Patron patron, @NonNull Document document) {
        val patronId = patron.getId();
        val documentId = document.getId();

        val patronAudit = patron.getAudit();
        val borrowedDocumentIdsOfPatron = patronAudit.getBorrowedDocumentIds();
        borrowedDocumentIdsOfPatron.remove(documentId);

        val documentAudit = document.getAudit();
        val borrowingPatronIdsOfDocument = documentAudit.getBorrowingPatronIds();
        borrowingPatronIdsOfDocument.remove(patronId);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull Patron patron;
        Document.@NonNull Id documentId;
    }

    public static class DocumentNotFoundException extends Exception {}
    public static class DocumentAlreadyReturnedException extends Exception {}
}
