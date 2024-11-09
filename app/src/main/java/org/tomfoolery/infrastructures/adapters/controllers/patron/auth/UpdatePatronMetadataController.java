package org.tomfoolery.infrastructures.adapters.controllers.patron.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.usecases.patron.auth.UpdatePatronMetadataUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class UpdatePatronMetadataController implements ThrowableConsumer<UpdatePatronMetadataController.RequestObject> {
    private final @NonNull UpdatePatronMetadataUseCase updatePatronMetadataUseCase;

    public static @NonNull UpdatePatronMetadataController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PatronRepository patronRepository) {
        return new UpdatePatronMetadataController(authenticationTokenGenerator, authenticationTokenRepository, patronRepository);
    }

    private UpdatePatronMetadataController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PatronRepository patronRepository) {
        this.updatePatronMetadataUseCase = UpdatePatronMetadataUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, patronRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws UpdatePatronMetadataUseCase.AuthenticationTokenNotFoundException, UpdatePatronMetadataUseCase.AuthenticationTokenInvalidException, UpdatePatronMetadataUseCase.PatronNotFoundException {
        val requestModel = requestObject.toRequestModel();
        this.updatePatronMetadataUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String patronFullName;
        @NonNull String patronAddress;
        @NonNull String patronEmail;

        private UpdatePatronMetadataUseCase.@NonNull Request toRequestModel() {
            val patronMetadata = Patron.Metadata.of(patronFullName, patronAddress, patronEmail);

            return UpdatePatronMetadataUseCase.Request.of(patronMetadata);
        }
    }
}
