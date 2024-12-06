package org.tomfoolery.infrastructures.adapters.controllers.common.documents.retrieval;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.common.documents.retrieval.GetDocumentByIdUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.TemporaryFileProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.resources.ResourceProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;
import org.tomfoolery.infrastructures.utils.helpers.adapters.TimestampBiAdapter;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

import java.io.IOException;
import java.util.List;

public final class GetDocumentByIdController implements ThrowableFunction<GetDocumentByIdController.RequestObject, GetDocumentByIdController.ViewModel> {
    private static final @NonNull String DEFAULT_COVER_IMAGE_RESOURCE_PATH = "images/default/document-cover-image.png";

    private final @NonNull GetDocumentByIdUseCase getDocumentByIdUseCase;

    public static @NonNull GetDocumentByIdController of(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentByIdController(hybridDocumentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentByIdController(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.getDocumentByIdUseCase = GetDocumentByIdUseCase.of(hybridDocumentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws GetDocumentByIdUseCase.AuthenticationTokenNotFoundException, GetDocumentByIdUseCase.AuthenticationTokenInvalidException, GetDocumentByIdUseCase.DocumentISBNInvalidException, GetDocumentByIdUseCase.DocumentNotFoundException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.getDocumentByIdUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static GetDocumentByIdUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return GetDocumentByIdUseCase.Request.of(requestObject.getDocumentISBN());
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(GetDocumentByIdUseCase.@NonNull Response responseModel) {
        return ViewModel.of(responseModel.getDocument());
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String documentISBN;
    }

    @Value
    @Builder(access = AccessLevel.PRIVATE)
    public static class ViewModel {
        @NonNull String documentISBN_10;
        @NonNull String documentISBN_13;

        @NonNull String createdTimestamp;
        @NonNull String lastModifiedTimestamp;
        @NonNull String createdByStaffId;
        @NonNull String lastModifiedByStaffId;

        @NonNull String documentTitle;
        @NonNull String documentDescription;
        @NonNull List<String> documentAuthors;
        @NonNull List<String> documentGenres;
        @Unsigned short documentPublishedYear;
        @NonNull String documentPublisher;

        @Unsigned double averageRating;
        @Unsigned int numberOfRatings;

        @NonNull String documentCoverImageFilePath;

        public static @NonNull ViewModel of(@NonNull Document document) {
            val documentId = document.getId();
            val documentAudit = document.getAudit();
            val documentAuditTimestamps = documentAudit.getTimestamps();
            val documentMetadata = document.getMetadata();
            val documentRating = document.getRating();
            val documentCoverImage = document.getCoverImage();

            return ViewModel.builder()
                .documentISBN_10(documentId.getISBN_10())
                .documentISBN_13(documentId.getISBN_13())

                .createdTimestamp(TimestampBiAdapter.serialize(documentAuditTimestamps.getCreated()))
                .lastModifiedTimestamp(documentAuditTimestamps.getLastModified() == null ? "null"
                    : TimestampBiAdapter.serialize(documentAuditTimestamps.getLastModified()))
                .createdByStaffId(UserIdBiAdapter.serialize(documentAudit.getCreatedByStaffId()))
                .lastModifiedByStaffId(documentAudit.getLastModifiedByStaffId() == null ? "null"
                    : UserIdBiAdapter.serialize(documentAudit.getLastModifiedByStaffId()))

                .documentTitle(documentMetadata.getTitle())
                .documentDescription(documentMetadata.getDescription())
                .documentAuthors(documentMetadata.getAuthors())
                .documentGenres(documentMetadata.getGenres())
                .documentPublishedYear((short) documentMetadata.getPublishedYear().getValue())
                .documentPublisher(documentMetadata.getPublisher())

                .averageRating(documentRating == null ? 0 : documentRating.getAverageRating())
                .numberOfRatings(documentRating == null ? 0 : documentRating.getNumberOfRatings())

                .documentCoverImageFilePath(saveDocumentCoverImageAndGetPath(
                    documentCoverImage == null ? new byte[0] : documentCoverImage.getBytes()
                ))

                .build();
        }

        @SneakyThrows
        private static @NonNull String saveDocumentCoverImageAndGetPath(byte @NonNull [] rawDocumentCoverImage) {
            try {
                return TemporaryFileProvider.save(".png", rawDocumentCoverImage);

            } catch (IOException exception) {
                return ResourceProvider.getResourceAbsolutePath(DEFAULT_COVER_IMAGE_RESOURCE_PATH);
            }
        }
    }
}
