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
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

public final class UpdatePatronPasswordController implements ThrowableConsumer<UpdatePatronPasswordController.RequestObject> {
    private final @NonNull UpdatePatronPasswordUseCase updatePatronPasswordUseCase;

    public static @NonNull UpdatePatronPasswordController of(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new UpdatePatronPasswordController(patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private UpdatePatronPasswordController(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.updatePatronPasswordUseCase = UpdatePatronPasswordUseCase.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws UpdatePatronPasswordUseCase.AuthenticationTokenNotFoundException, UpdatePatronPasswordUseCase.AuthenticationTokenInvalidException, UpdatePatronPasswordUseCase.PatronNotFoundException, UpdatePatronPasswordUseCase.PasswordInvalidException, UpdatePatronPasswordUseCase.PasswordMismatchException {
        val requestModel = requestObject.toRequestModel();
        this.updatePatronPasswordUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        char @NonNull [] oldPatronPassword;
        char @NonNull [] newPatronPassword;

        private UpdatePatronPasswordUseCase.@NonNull Request toRequestModel() {
            val oldPatronPassword = SecureString.of(this.oldPatronPassword);
            val newPatronPassword = SecureString.of(this.newPatronPassword);

            return UpdatePatronPasswordUseCase.Request.of(oldPatronPassword, newPatronPassword);
        }
    }
}
