package org.tomfoolery.infrastructures.adapters.controllers.patron.users.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.usecases.patron.users.persistence.UpdatePatronMetadataUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

import java.time.LocalDate;

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
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.updatePatronMetadataUseCase.accept(requestModel);
    }

    private static UpdatePatronMetadataUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        val patronName = Patron.Metadata.Name.of(requestObject.getPatronFirstName(), requestObject.getPatronLastName());
        val patronDateOfBirth = LocalDate.of(requestObject.getPatronYearOfBirth(), requestObject.getPatronMonthOfBirth(), requestObject.getPatronDayOfBirth());
        val patronPhoneNumber = requestObject.getPatronPhoneNumber();
        val patronAddress = Patron.Metadata.Address.of(requestObject.getPatronCity(), requestObject.getPatronCountry());
        val patronEmail = requestObject.getPatronEmail();

        val patronMetadata = Patron.Metadata.of(patronName, patronDateOfBirth, patronPhoneNumber, patronAddress, patronEmail);
        return UpdatePatronMetadataUseCase.Request.of(patronMetadata);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
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
