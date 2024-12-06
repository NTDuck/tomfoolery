package org.tomfoolery.infrastructures.adapters.controllers.patron.documents.borrow.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.patron.documents.borrow.retrieval.ReadBorrowedDocumentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.TemporaryFileProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;

import java.io.IOException;

public final class ReadBorrowedDocumentController implements ThrowableFunction<ReadBorrowedDocumentController.RequestObject, ReadBorrowedDocumentController.ViewModel> {
    private final @NonNull ReadBorrowedDocumentUseCase readBorrowedDocumentUseCase;

    public static @NonNull ReadBorrowedDocumentController of(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReadBorrowedDocumentController(hybridDocumentRepository, documentContentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ReadBorrowedDocumentController(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.readBorrowedDocumentUseCase = ReadBorrowedDocumentUseCase.of(hybridDocumentRepository, documentContentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws ReadBorrowedDocumentUseCase.AuthenticationTokenNotFoundException, ReadBorrowedDocumentUseCase.AuthenticationTokenInvalidException, ReadBorrowedDocumentUseCase.DocumentISBNInvalidException, ReadBorrowedDocumentUseCase.DocumentNotFoundException, ReadBorrowedDocumentUseCase.DocumentNotBorrowedException, ReadBorrowedDocumentUseCase.DocumentOverdueException, ReadBorrowedDocumentUseCase.DocumentContentNotFoundException, DocumentContentUnavailable {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.readBorrowedDocumentUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static ReadBorrowedDocumentUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return ReadBorrowedDocumentUseCase.Request.of(requestObject.getDocumentISBN());
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(ReadBorrowedDocumentUseCase.@NonNull Response responseModel) throws DocumentContentUnavailable {
        val rawDocumentContent = responseModel.getDocumentContent().getBytes();
        val documentContentFilePath = saveDocumentContentAndGetPath(rawDocumentContent);

        return ViewModel.of(documentContentFilePath);
    }

    private static @NonNull String saveDocumentContentAndGetPath(byte @NonNull [] rawDocumentContent) throws DocumentContentUnavailable {
        try {
            return TemporaryFileProvider.save(".pdf", rawDocumentContent);
        } catch (IOException exception) {
            throw new DocumentContentUnavailable();
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

    public static class DocumentContentUnavailable extends Exception {}
}
