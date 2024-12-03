package org.tomfoolery.infrastructures.adapters.controllers.patron.users.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.patron.users.retrieval.GetPatronUsernameAndMetadataUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableSupplier;

public final class GetPatronUsernameAndMetadataController implements ThrowableSupplier<GetPatronUsernameAndMetadataController.ViewModel> {
    private final @NonNull GetPatronUsernameAndMetadataUseCase getPatronUsernameAndMetadataUseCase;

    public static @NonNull GetPatronUsernameAndMetadataController of(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetPatronUsernameAndMetadataController(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetPatronUsernameAndMetadataController(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.getPatronUsernameAndMetadataUseCase = GetPatronUsernameAndMetadataUseCase.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel get() throws GetPatronUsernameAndMetadataUseCase.AuthenticationTokenNotFoundException, GetPatronUsernameAndMetadataUseCase.AuthenticationTokenInvalidException, GetPatronUsernameAndMetadataUseCase.PatronNotFoundException {
        val responseModel = this.getPatronUsernameAndMetadataUseCase.get();
        val viewModel = ViewModel.fromResponseModel(responseModel);

        return viewModel;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull String patronUserName;

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
