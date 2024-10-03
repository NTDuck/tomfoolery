package org.tomfoolery.core.usecases.documents.common;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.Document;

import java.util.Collection;

public class SearchDocumentsByAuthorUseCase {
    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentAuthor;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Collection<Document> documents;
    }
}
