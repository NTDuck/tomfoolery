package org.tomfoolery.infrastructures.adapters.controllers.staff.documents;

import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.staff.documents.AddDocumentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.infrastructures.utils.helpers.reflection.Cloner;

import java.time.Year;
import java.util.List;

public final class AddDocumentController implements ThrowableConsumer<AddDocumentController.RequestObject> {
    private final @NonNull AddDocumentUseCase addDocumentUseCase;

    public static @NonNull AddDocumentController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new AddDocumentController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private AddDocumentController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        this.addDocumentUseCase = AddDocumentUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws Exception {
        val requestModel = generateRequestModelFromRequestObject(requestObject);
        this.addDocumentUseCase.accept(requestModel);
    }

    @SneakyThrows
    private static AddDocumentUseCase.@NonNull Request generateRequestModelFromRequestObject(@NonNull RequestObject requestObject) {
        return Cloner.cloneFrom(requestObject, AddDocumentUseCase.Request.class);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;

        @NonNull String documentTitle;
        @NonNull String documentDescription;
        @NonNull List<String> documentAuthors;
        @NonNull List<String> documentGenres;

        @Unsigned short documentPublishedYear;
        @NonNull String documentPublisher;

        byte @NonNull [] rawDocumentContent;
        byte @NonNull [] rawDocumentCoverImage;

        private AddDocumentUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);
            val documentContent = Document.Content.of(rawDocumentContent);

            val documentMetadata = Document.Metadata.of(
                documentTitle, documentDescription, documentAuthors, documentGenres,
                Year.of(documentPublishedYear), documentPublisher, Document.Metadata.CoverImage.of(rawDocumentCoverImage)
            );

            return AddDocumentUseCase.Request.of(documentId, documentContent, documentMetadata);
        }
    }
}
