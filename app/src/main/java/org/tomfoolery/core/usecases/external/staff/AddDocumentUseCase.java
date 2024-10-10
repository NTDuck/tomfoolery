package org.tomfoolery.core.usecases.interactive.staff;

import lombok.*;

import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.Staff;
import org.tomfoolery.core.utils.function.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class AddDocumentUseCase implements ThrowableFunction<AddDocumentUseCase.Request, AddDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public Response apply(@NonNull Request request) throws DocumentAlreadyExistsException {
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
        @NonNull Document.ID documentId;
        @NonNull Document.Metadata documentMetadata;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Document.ID documentId;
    }

    public static class DocumentAlreadyExistsException extends Exception {}
}
