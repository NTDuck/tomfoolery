package org.tomfoolery.infrastructures.adapters.controllers.external.guest.users.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.external.guest.users.persistence.CreatePatronAccountUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;

import java.time.LocalDate;

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
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.createPatronAccountUseCase.accept(requestModel);
    }

    private static CreatePatronAccountUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        val patronPassword = SecureString.of(requestObject.getPatronPassword());
        val patronCredentials = Staff.Credentials.of(requestObject.getPatronUsername(), patronPassword);

        val patronName = Patron.Metadata.Name.of(requestObject.getPatronFirstName(), requestObject.getPatronLastName());
        val patronDateOfBirth = LocalDate.of(requestObject.getPatronYearOfBirth(), requestObject.getPatronMonthOfBirth(), requestObject.getPatronDayOfBirth());
        val patronPhoneNumber = requestObject.getPatronPhoneNumber();
        val patronAddress = Patron.Metadata.Address.of(requestObject.getPatronCity(), requestObject.getPatronCountry());
        val patronEmail = requestObject.getPatronEmail();

        val patronMetadata = Patron.Metadata.of(patronName, patronDateOfBirth, patronPhoneNumber, patronAddress, patronEmail);

        return CreatePatronAccountUseCase.Request.of(patronCredentials, patronMetadata);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String patronUsername;
        char @NonNull [] patronPassword;

        @NonNull String patronFirstName;
        @NonNull String patronLastName;

        @Unsigned int patronDayOfBirth;
        @Unsigned int patronMonthOfBirth;
        @Unsigned int patronYearOfBirth;

        @NonNull String patronPhoneNumber;

        @NonNull String patronCity;
        @NonNull String patronCountry;

        @NonNull String patronEmail;
    }
}
