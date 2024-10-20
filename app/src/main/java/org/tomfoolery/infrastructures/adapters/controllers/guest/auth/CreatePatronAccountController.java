package org.tomfoolery.infrastructures.adapters.controllers.guest.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.core.domain.Patron;
import org.tomfoolery.core.usecases.external.guest.auth.CreatePatronAccountUseCase;
import org.tomfoolery.infrastructures.utils.contracts.ThrowableConsumerController;

public class CreatePatronAccountController implements ThrowableConsumerController<CreatePatronAccountController.RequestObject, CreatePatronAccountUseCase.Request> {
    private final @NonNull CreatePatronAccountUseCase useCase;

    private CreatePatronAccountController(@NonNull PatronRepository patronRepository, @NonNull PasswordService passwordService) {
        this.useCase = CreatePatronAccountUseCase.of(patronRepository, passwordService);
    }

    public static @NonNull CreatePatronAccountController of(@NonNull PatronRepository patronRepository, @NonNull PasswordService passwordService) {
        return new CreatePatronAccountController(patronRepository, passwordService);
    }

    @Override
    public CreatePatronAccountUseCase.@NonNull Request getRequestModelFromRequestObject(@NonNull RequestObject requestObject) {
        val username = requestObject.getUsername();
        val password = requestObject.getPassword();

        val credentials = Patron.Credentials.of(username, password);

        val firstName = requestObject.getFirstName();
        val lastName = requestObject.getLastName();
        val address = requestObject.getAddress();
        val gmail = requestObject.getGmail();

        val fullName = getFullName(firstName, lastName);
        val metadata = Patron.Metadata.of(fullName, address, gmail);

        return CreatePatronAccountUseCase.Request.of(credentials, metadata);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws CreatePatronAccountUseCase.PatronCredentialsInvalidException, CreatePatronAccountUseCase.PatronAlreadyExistsException {
        val requestModel = getRequestModelFromRequestObject(requestObject);
        this.useCase.accept(requestModel);
    }

    private static @NonNull String getFullName(@NonNull String firstName, @NonNull String lastName) {
        return firstName + " " + lastName;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String username;
        @NonNull String password;

        @NonNull String firstName;
        @NonNull String lastName;
        @NonNull String address;
        @NonNull String gmail;
    }
}
