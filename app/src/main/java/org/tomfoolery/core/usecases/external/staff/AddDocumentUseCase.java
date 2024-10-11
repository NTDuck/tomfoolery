package org.tomfoolery.core.usecases.external.staff;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.Staff;
import org.tomfoolery.core.utils.function.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class AddDocumentUseCase implements ThrowableFunction<AddDocumentUseCase.Request, AddDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public @NonNull Response apply(@NonNull Request request) throws DocumentAlreadyExistsException {
        val documentId = request.getDocumentId();

        if (this.documentRepository.contains(documentId))
            throw new DocumentAlreadyExistsException();

        val staff = request.getStaff();
        val staffId = staff.getId();

        val documentMetadata = request.getDocumentMetadata();
        val documentAudit = Document.Audit.of(staffId);

        val document = Document.of(documentId, documentMetadata, documentAudit);
        this.documentRepository.save(document);

        return Response.of(documentId);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull Staff staff;
        Document.@NonNull Id documentId;
        Document.@NonNull Metadata documentMetadata;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        Document.@NonNull Id documentId;
    }

    public static class DocumentAlreadyExistsException extends Exception {}
}
