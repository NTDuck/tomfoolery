package org.tomfoolery.infrastructures.adapters.controllers.patron.documents.rating;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.patron.documents.rating.AddDocumentRatingUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class AddDocumentRatingController implements ThrowableConsumer<AddDocumentRatingController.RequestObject> {
    private final @NonNull AddDocumentRatingUseCase addDocumentRatingUseCase;

    public static @NonNull AddDocumentRatingController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        return new AddDocumentRatingController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, patronRepository);
    }

    private AddDocumentRatingController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        this.addDocumentRatingUseCase = AddDocumentRatingUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, patronRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws AddDocumentRatingUseCase.AuthenticationTokenNotFoundException, AddDocumentRatingUseCase.AuthenticationTokenInvalidException, AddDocumentRatingUseCase.PatronNotFoundException, AddDocumentRatingUseCase.DocumentNotFoundException, AddDocumentRatingUseCase.RatingValueInvalidException, AddDocumentRatingUseCase.PatronRatingAlreadyExistsException {
        val requestModel = requestObject.toRequestModel();
        this.addDocumentRatingUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;
        @Unsigned double rating;

        private AddDocumentRatingUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);

            return AddDocumentRatingUseCase.Request.of(documentId, rating);
        }
    }
}
