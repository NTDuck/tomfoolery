package org.tomfoolery.infrastructures.adapters.controllers.staff.documents;

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

import java.time.Year;
import java.util.List;

public final class AddDocumentController implements ThrowableConsumer<AddDocumentController.RequestObject> {
    private final @NonNull AddDocumentUseCase addDocumentUseCase;

    public static @NonNull AddDocumentController of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new AddDocumentController(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private AddDocumentController(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.addDocumentUseCase = AddDocumentUseCase.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws AddDocumentUseCase.AuthenticationTokenNotFoundException, AddDocumentUseCase.AuthenticationTokenInvalidException, AddDocumentUseCase.DocumentAlreadyExistsException {
        val requestModel = requestObject.toRequestModel();
        this.addDocumentUseCase.accept(requestModel);
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

        byte @NonNull [] documentContent;
        byte @NonNull [] documentCoverImage;

        private AddDocumentUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);
            val documentContent = Document.Content.of(this.documentContent);
            val documentMetadata = Document.Metadata.of(
                documentTitle, documentDescription, documentAuthors, documentGenres,
                Year.of(documentPublishedYear), documentPublisher, Document.Metadata.CoverImage.of(documentCoverImage)
            );

            return AddDocumentUseCase.Request.of(documentId, documentContent, documentMetadata);
        }
    }
}
