package org.tomfoolery.core.usecases.interactive.user;

import lombok.*;

import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;

import java.util.Collection;
import java.util.function.Supplier;

@RequiredArgsConstructor(staticName = "of")
public class ShowDocumentsUseCase implements Supplier<ShowDocumentsUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    @Override
    public Response get() {
        val documents = this.documentRepository.show();
        return Response.of(documents);
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Collection<Document> documents;
    }
}
