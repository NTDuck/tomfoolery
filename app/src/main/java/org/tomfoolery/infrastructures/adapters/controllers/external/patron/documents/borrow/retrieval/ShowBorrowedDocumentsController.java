package org.tomfoolery.infrastructures.adapters.controllers.external.patron.documents.borrow.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.external.patron.documents.borrow.retrieval.ShowBorrowedDocumentsUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.retrieval.GetDocumentByIdController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class ShowBorrowedDocumentsController implements ThrowableFunction<ShowBorrowedDocumentsController.RequestObject, ShowBorrowedDocumentsController.ViewModel> {
    private final @NonNull ShowBorrowedDocumentsUseCase showBorrowedDocumentsUseCase;
    private final @NonNull FileStorageProvider fileStorageProvider;

    public static @NonNull ShowBorrowedDocumentsController of(@NonNull DocumentRepository documentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        return new ShowBorrowedDocumentsController(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    private ShowBorrowedDocumentsController(@NonNull DocumentRepository documentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        this.showBorrowedDocumentsUseCase = ShowBorrowedDocumentsUseCase.of(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws ShowBorrowedDocumentsUseCase.AuthenticationTokenInvalidException, ShowBorrowedDocumentsUseCase.AuthenticationTokenNotFoundException, ShowBorrowedDocumentsUseCase.PaginationInvalidException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.showBorrowedDocumentsUseCase.apply(requestModel);
        val viewModel = this.mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static ShowBorrowedDocumentsUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return ShowBorrowedDocumentsUseCase.Request.of(requestObject.getPageIndex(), requestObject.getMaxPageSize());
    }

    private @NonNull ViewModel mapResponseModelToViewModel(ShowBorrowedDocumentsUseCase.@NonNull Response responseModel) {
        val page = responseModel.getPaginatedBorrowedDocuments();

        val builder = GetDocumentByIdController.ViewModel.Builder_.with(this.fileStorageProvider);
        val paginatedBorrowedDocuments = StreamSupport.stream(page.spliterator(), true)
            .map(builder::build)
            .collect(Collectors.toUnmodifiableList());

        val pageIndex = page.getPageIndex();
        val maxPageIndex = page.getMaxPageIndex();

        return ViewModel.of(paginatedBorrowedDocuments, pageIndex, maxPageIndex);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull List<GetDocumentByIdController.ViewModel> paginatedBorrowedDocuments;

        @Unsigned int pageIndex;
        @Unsigned int maxPageIndex;
    }
}
