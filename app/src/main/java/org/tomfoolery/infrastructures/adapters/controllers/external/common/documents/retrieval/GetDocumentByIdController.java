package org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.retrieval;

import lombok.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.external.common.documents.retrieval.GetDocumentByIdUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.resources.ResourceProvider;
import org.tomfoolery.core.dataproviders.repositories.aggregates.hybrids.documents.HybridDocumentRepository;
import org.tomfoolery.infrastructures.utils.helpers.adapters.TimestampBiAdapter;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

import java.io.IOException;
import java.util.List;

public final class GetDocumentByIdController implements ThrowableFunction<GetDocumentByIdController.RequestObject, GetDocumentByIdController.ViewModel> {
    private static final @NonNull String DEFAULT_COVER_IMAGE_RESOURCE_PATH = "images/default/document-cover-image.png";

    private final @NonNull GetDocumentByIdUseCase getDocumentByIdUseCase;
    private final @NonNull FileStorageProvider fileStorageProvider;

    public static @NonNull GetDocumentByIdController of(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        return new GetDocumentByIdController(hybridDocumentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    private GetDocumentByIdController(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        this.getDocumentByIdUseCase = GetDocumentByIdUseCase.of(hybridDocumentRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws GetDocumentByIdUseCase.AuthenticationTokenNotFoundException, GetDocumentByIdUseCase.AuthenticationTokenInvalidException, GetDocumentByIdUseCase.DocumentISBNInvalidException, GetDocumentByIdUseCase.DocumentNotFoundException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.getDocumentByIdUseCase.apply(requestModel);
        val viewModel = this.mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static GetDocumentByIdUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return GetDocumentByIdUseCase.Request.of(requestObject.getDocumentISBN());
    }

    private @NonNull ViewModel mapResponseModelToViewModel(GetDocumentByIdUseCase.@NonNull Response responseModel) {
        return ViewModel.of(responseModel.getDocument(), this.fileStorageProvider);
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

        @SneakyThrows
        private static @NonNull ViewModel of(@NonNull Document document, @NonNull FileStorageProvider fileStorageProvider) {
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

                .documentCoverImageFilePath(documentCoverImage != null
                    ? saveDocumentCoverImageAndGetPath(documentCoverImage, fileStorageProvider)
                    : ResourceProvider.getResourceAbsolutePath(DEFAULT_COVER_IMAGE_RESOURCE_PATH))

                .build();
        }

        @SneakyThrows
        private static @NonNull String saveDocumentCoverImageAndGetPath(Document.@NonNull CoverImage documentCoverImage, @NonNull FileStorageProvider fileStorageProvider) {
            try {
                val rawDocumentCoverImage = documentCoverImage.getBytes();
                return fileStorageProvider.save(rawDocumentCoverImage);

            } catch (IOException exception) {
                return ResourceProvider.getResourceAbsolutePath(DEFAULT_COVER_IMAGE_RESOURCE_PATH);
            }
        }

        @Value(staticConstructor = "with")
        public static class Builder_ {
            @Getter(value = AccessLevel.NONE)
            @NonNull FileStorageProvider fileStorageProvider;

            public @NonNull ViewModel build(@NonNull Document document) {
                return ViewModel.of(document, this.fileStorageProvider);
            }
        }
    }
}
