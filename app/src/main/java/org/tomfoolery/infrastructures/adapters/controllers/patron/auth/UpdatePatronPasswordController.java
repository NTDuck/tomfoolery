package org.tomfoolery.infrastructures.adapters.controllers.patron.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.patron.auth.UpdatePatronPasswordUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class UpdatePatronPasswordController implements ThrowableConsumer<UpdatePatronPasswordController.RequestObject> {
    private final @NonNull UpdatePatronPasswordUseCase updatePatronPasswordUseCase;

    public static @NonNull UpdatePatronPasswordController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new UpdatePatronPasswordController(authenticationTokenGenerator, authenticationTokenRepository, patronRepository, passwordEncoder);
    }

    private UpdatePatronPasswordController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.updatePatronPasswordUseCase = UpdatePatronPasswordUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, patronRepository, passwordEncoder);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws UpdatePatronPasswordUseCase.AuthenticationTokenNotFoundException, UpdatePatronPasswordUseCase.AuthenticationTokenInvalidException, UpdatePatronPasswordUseCase.PatronNotFoundException, UpdatePatronPasswordUseCase.PasswordInvalidException, UpdatePatronPasswordUseCase.PasswordMismatchException {
        val requestModel = requestObject.toRequestModel();
        this.updatePatronPasswordUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String oldPatronPassword;
        @NonNull String newPatronPassword;

        private UpdatePatronPasswordUseCase.@NonNull Request toRequestModel() {
            return UpdatePatronPasswordUseCase.Request.of(oldPatronPassword, newPatronPassword);
        }
    }
}
