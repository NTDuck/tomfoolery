package org.tomfoolery.core.usecases.documents.common;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.Document;

public class GetDocumentByIdUseCase {
    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull Document.ID documentId;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Document document;
    }

    public static class DocumentNotFoundException extends Exception {}
}
