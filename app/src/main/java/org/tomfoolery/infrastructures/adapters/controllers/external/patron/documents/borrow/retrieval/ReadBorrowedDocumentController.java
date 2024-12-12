package org.tomfoolery.infrastructures.adapters.controllers.external.patron.documents.borrow.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.patron.documents.borrow.retrieval.ReadBorrowedDocumentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;
import org.tomfoolery.core.dataproviders.repositories.aggregates.hybrids.documents.HybridDocumentRepository;

import java.io.IOException;

public final class ReadBorrowedDocumentController implements ThrowableFunction<ReadBorrowedDocumentController.RequestObject, ReadBorrowedDocumentController.ViewModel> {
    private final @NonNull ReadBorrowedDocumentUseCase readBorrowedDocumentUseCase;
    private final @NonNull FileStorageProvider fileStorageProvider;

    public static @NonNull ReadBorrowedDocumentController of(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        return new ReadBorrowedDocumentController(hybridDocumentRepository, documentContentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    private ReadBorrowedDocumentController(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        this.readBorrowedDocumentUseCase = ReadBorrowedDocumentUseCase.of(hybridDocumentRepository, documentContentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
         this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws ReadBorrowedDocumentUseCase.AuthenticationTokenNotFoundException, ReadBorrowedDocumentUseCase.AuthenticationTokenInvalidException, ReadBorrowedDocumentUseCase.DocumentISBNInvalidException, ReadBorrowedDocumentUseCase.DocumentNotFoundException, ReadBorrowedDocumentUseCase.DocumentNotBorrowedException, ReadBorrowedDocumentUseCase.DocumentOverdueException, ReadBorrowedDocumentUseCase.DocumentContentNotFoundException, DocumentContentFileWriteException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.readBorrowedDocumentUseCase.apply(requestModel);
        val viewModel = this.mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static ReadBorrowedDocumentUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return ReadBorrowedDocumentUseCase.Request.of(requestObject.getDocumentISBN());
    }

    private @NonNull ViewModel mapResponseModelToViewModel(ReadBorrowedDocumentUseCase.@NonNull Response responseModel) throws DocumentContentFileWriteException {
        val rawDocumentContent = responseModel.getDocumentContent().getBytes();
        val documentContentFilePath = this.saveDocumentContentAndGetPath(rawDocumentContent);

        return ViewModel.of(documentContentFilePath);
    }

    private @NonNull String saveDocumentContentAndGetPath(byte @NonNull [] rawDocumentContent) throws DocumentContentFileWriteException {
        try {
            return this.fileStorageProvider.save(rawDocumentContent);
        } catch (IOException exception) {
            throw new DocumentContentFileWriteException();
        }
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String documentISBN;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull String documentContentFilePath;
    }

    public static class DocumentContentFileWriteException extends Exception {}
}
