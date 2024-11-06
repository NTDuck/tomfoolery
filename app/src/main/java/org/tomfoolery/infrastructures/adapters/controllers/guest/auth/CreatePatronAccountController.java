package org.tomfoolery.infrastructures.adapters.controllers.guest.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.guest.auth.CreatePatronAccountUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public class CreatePatronAccountController implements ThrowableConsumer<CreatePatronAccountController.Request> {
    private final @NonNull CreatePatronAccountUseCase useCase;

    public static @NonNull CreatePatronAccountController of(@NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new CreatePatronAccountController(patronRepository, passwordEncoder);
    }

    private CreatePatronAccountController(@NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.useCase = CreatePatronAccountUseCase.of(patronRepository, passwordEncoder);
    }

    @Override
    public void accept(@NonNull Request requestObject) throws CreatePatronAccountUseCase.PatronCredentialsInvalidException, CreatePatronAccountUseCase.PatronAlreadyExistsException {
        val requestModel = generateRequestModelFromRequestObject(requestObject);
        this.useCase.accept(requestModel);
    }

    private static CreatePatronAccountUseCase.@NonNull Request generateRequestModelFromRequestObject(@NonNull Request requestObject) {
        val patronCredentials = BaseUser.Credentials.of(requestObject.getUsername(), requestObject.getPassword());
        val patronMetadata = Patron.Metadata.of(requestObject.getFullName(), requestObject.getAddress(), requestObject.getEmail());
        
        return CreatePatronAccountUseCase.Request.of(patronCredentials, patronMetadata);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String username;
        @NonNull String password;

        @NonNull String fullName;
        @NonNull String address;
        @NonNull String email;
    }
}
