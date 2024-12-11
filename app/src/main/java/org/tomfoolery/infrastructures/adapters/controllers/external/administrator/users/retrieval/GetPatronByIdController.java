package org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.usecases.external.administrator.users.retrieval.GetAdministratorByIdUseCase;
import org.tomfoolery.core.usecases.external.administrator.users.retrieval.GetPatronByIdUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.utils.helpers.adapters.TimestampBiAdapter;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

public final class GetPatronByIdController implements ThrowableFunction<GetPatronByIdController.RequestObject, GetPatronByIdController.ViewModel> {
    private final @NonNull GetPatronByIdUseCase getPatronByIdUseCase;

    public static @NonNull GetPatronByIdController of(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetPatronByIdController(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetPatronByIdController(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.getPatronByIdUseCase = GetPatronByIdUseCase.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws UserIdBiAdapter.UserUuidInvalidException, GetPatronByIdUseCase.AuthenticationTokenNotFoundException, GetPatronByIdUseCase.AuthenticationTokenInvalidException, GetPatronByIdUseCase.UserNotFoundException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.getPatronByIdUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static GetPatronByIdUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) throws UserIdBiAdapter.UserUuidInvalidException {
        val patronId = UserIdBiAdapter.parse(requestObject.getPatronUuid());

        return GetAdministratorByIdUseCase.Request.of(patronId);
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(GetPatronByIdUseCase.@NonNull Response<Patron> responseModel) {
        return ViewModel.of(responseModel.getUser());
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String patronUuid;
    }

    @Value
    @Builder(access = AccessLevel.PRIVATE)
    public static class ViewModel {
        @NonNull String patronUuid;
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

        @NonNull String creationTimestamp;
        @NonNull String lastModifiedTimestamp;
        @NonNull String lastLoginTimestamp;
        @NonNull String lastLogoutTimestamp;

        public static @NonNull ViewModel of(@NonNull Patron patron) {
            val patronMetadata = patron.getMetadata();
            val patronAuditTimestamps = patron.getAudit().getTimestamps();

            return ViewModel.builder()
                .patronUuid(UserIdBiAdapter.serialize(patron.getId()))
                .patronUsername(patron.getCredentials().getUsername())

                .patronFirstName(patronMetadata.getName().getFirstName())
                .patronLastName(patronMetadata.getName().getLastName())
                .patronDayOfBirth(patronMetadata.getDateOfBirth().getDayOfMonth())
                .patronMonthOfBirth(patronMetadata.getDateOfBirth().getMonthValue())
                .patronYearOfBirth(patronMetadata.getDateOfBirth().getYear())
                .patronPhoneNumber(patronMetadata.getPhoneNumber())
                .patronCity(patronMetadata.getAddress().getCity())
                .patronCountry(patronMetadata.getAddress().getCountry())
                .patronEmail(patronMetadata.getEmail())

                .creationTimestamp(TimestampBiAdapter.serialize(patronAuditTimestamps.getCreated()))
                .lastModifiedTimestamp(patronAuditTimestamps.getLastModified() == null ? "null"
                    : TimestampBiAdapter.serialize(patronAuditTimestamps.getLastModified()))
                .lastLoginTimestamp(patronAuditTimestamps.getLastLogin() == null ? "null"
                    : TimestampBiAdapter.serialize(patronAuditTimestamps.getLastLogin()))
                .lastLogoutTimestamp(patronAuditTimestamps.getLastLogout() == null ? "null"
                    : TimestampBiAdapter.serialize(patronAuditTimestamps.getLastLogout()))
                .build();
        }
    }
}