package org.tomfoolery.infrastructures.adapters.controllers.patron.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.usecases.patron.users.persistence.UpdatePatronMetadataUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class UpdatePatronMetadataController implements ThrowableConsumer<UpdatePatronMetadataController.RequestObject> {
    private final @NonNull UpdatePatronMetadataUseCase updatePatronMetadataUseCase;

    public static @NonNull UpdatePatronMetadataController of(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new UpdatePatronMetadataController(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private UpdatePatronMetadataController(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.updatePatronMetadataUseCase = UpdatePatronMetadataUseCase.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws UpdatePatronMetadataUseCase.AuthenticationTokenNotFoundException, UpdatePatronMetadataUseCase.AuthenticationTokenInvalidException, UpdatePatronMetadataUseCase.PatronNotFoundException {
        val requestModel = requestObject.toRequestModel();
        this.updatePatronMetadataUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String patronFirstName;
        @NonNull String patronLastName;

        @NonNull String patronAddress;
        @NonNull String patronEmail;

        private UpdatePatronMetadataUseCase.@NonNull Request toRequestModel() {
            val patronFullName = String.format("%s %s", patronFirstName, patronLastName);
            val patronMetadata = Patron.Metadata.of(patronFullName, patronAddress, patronEmail);

            return UpdatePatronMetadataUseCase.Request.of(patronMetadata);
        }
    }
}
