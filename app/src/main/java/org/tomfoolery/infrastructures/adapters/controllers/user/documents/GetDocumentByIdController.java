package org.tomfoolery.infrastructures.adapters.controllers.user.documents;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.user.documents.GetDocumentByIdUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

import java.util.List;

public final class GetDocumentByIdController implements ThrowableFunction<GetDocumentByIdController.RequestObject, GetDocumentByIdController.ViewModel> {
    private final @NonNull GetDocumentByIdUseCase getDocumentByIdUseCase;

    public static @NonNull GetDocumentByIdController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new GetDocumentByIdController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private GetDocumentByIdController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        this.getDocumentByIdUseCase = GetDocumentByIdUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws Exception {
        val requestModel = requestObject.toRequestModel();
        val responseModel = this.getDocumentByIdUseCase.apply(requestModel);
        val viewModel = ViewModel.fromResponseModel(responseModel);

        return viewModel;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;

        private GetDocumentByIdUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);

            return GetDocumentByIdUseCase.Request.of(documentId);
        }
    }

    @Value(staticConstructor = "of")
    @Builder(access = AccessLevel.PRIVATE)
    public static class ViewModel {
        @NonNull String ISBN;

        @NonNull String documentTitle;
        @NonNull String documentDescription;
        @NonNull List<String> documentAuthors;
        @NonNull List<String> documentGenres;

        @Unsigned short documentPublishedYear;
        @NonNull String documentPublisher;

        byte @NonNull [] rawDocumentCoverImage;

        @Unsigned long numberOfBorrowingPatrons;
        @Unsigned long numberOfRatings;
        @Unsigned double averageRating;

        private static @NonNull ViewModel fromResponseModel(GetDocumentByIdUseCase.@NonNull Response responseModel) {
            val fragmentaryDocument = responseModel.getFragmentaryDocument();

            val documentId = fragmentaryDocument.getId();
            val documentMetadata = fragmentaryDocument.getMetadata();
            val documentAudit = fragmentaryDocument.getAudit();

            return ViewModel.builder()
                .ISBN(documentId.getISBN())

                .documentTitle(documentMetadata.getTitle())
                .documentDescription(documentMetadata.getDescription())
                .documentAuthors(documentMetadata.getAuthors())
                .documentGenres(documentMetadata.getGenres())

                .documentPublishedYear((short) documentMetadata.getPublishedYear().getValue())
                .documentPublisher(documentMetadata.getPublisher())

                .numberOfBorrowingPatrons(documentAudit.getBorrowingPatronIds().size())
                .numberOfRatings(documentAudit.getRating().getRatingCount())
                .averageRating(documentAudit.getRating().getRatingValue())

                .build();
        }
    }
}
