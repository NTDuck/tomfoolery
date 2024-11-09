package org.tomfoolery.infrastructures.adapters.controllers.guest.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.usecases.guest.auth.CreatePatronAccountUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class CreatePatronAccountController implements ThrowableConsumer<CreatePatronAccountController.RequestObject> {
    private final @NonNull CreatePatronAccountUseCase createPatronAccountUseCase;

    public static @NonNull CreatePatronAccountController of(@NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new CreatePatronAccountController(patronRepository, passwordEncoder);
    }

    private CreatePatronAccountController(@NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.createPatronAccountUseCase = CreatePatronAccountUseCase.of(patronRepository, passwordEncoder);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws CreatePatronAccountUseCase.PatronCredentialsInvalidException, CreatePatronAccountUseCase.PatronAlreadyExistsException {
        val requestModel = requestObject.toRequestModel();
        this.createPatronAccountUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String patronUsername;
        @NonNull String patronPassword;

        @NonNull String patronFullName;
        @NonNull String patronAddress;
        @NonNull String patronEmail;

        private CreatePatronAccountUseCase.@NonNull Request toRequestModel() {
            val patronCredentials = Patron.Credentials.of(patronUsername, patronPassword);
            val patronMetadata = Patron.Metadata.of(patronFullName, patronAddress, patronEmail);

            return CreatePatronAccountUseCase.Request.of(patronCredentials, patronMetadata);
        }
    }
}
