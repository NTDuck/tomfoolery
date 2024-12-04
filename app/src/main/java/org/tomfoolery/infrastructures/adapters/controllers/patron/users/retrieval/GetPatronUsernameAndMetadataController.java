package org.tomfoolery.infrastructures.adapters.controllers.patron.users.retrieval;

import lombok.AccessLevel;
import lombok.Builder;
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
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(GetPatronUsernameAndMetadataUseCase.@NonNull Response responseModel) {
        return ViewModel.builder()
            .patronUsername(responseModel.getPatronUsername())

            .patronFirstName(responseModel.getPatronMetadata().getName().getFirstName())
            .patronLastName(responseModel.getPatronMetadata().getName().getLastName())
            .patronDayOfBirth(responseModel.getPatronMetadata().getDateOfBirth().getDayOfMonth())
            .patronMonthOfBirth(responseModel.getPatronMetadata().getDateOfBirth().getMonthValue())
            .patronYearOfBirth(responseModel.getPatronMetadata().getDateOfBirth().getYear())
            .patronPhoneNumber(responseModel.getPatronMetadata().getPhoneNumber())
            .patronCity(responseModel.getPatronMetadata().getAddress().getCity())
            .patronCountry(responseModel.getPatronMetadata().getAddress().getCountry())
            .patronEmail(responseModel.getPatronMetadata().getEmail())

            .build();
    }

    @Value(staticConstructor = "of")
    @Builder(access = AccessLevel.PRIVATE)
    public static class ViewModel {
        @NonNull String patronUsername;

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
