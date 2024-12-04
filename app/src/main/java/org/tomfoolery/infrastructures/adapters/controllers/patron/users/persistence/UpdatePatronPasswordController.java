package org.tomfoolery.infrastructures.adapters.controllers.patron.users.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.patron.users.persistence.UpdatePatronPasswordUseCase;
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
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.updatePatronPasswordUseCase.accept(requestModel);
    }

    private static UpdatePatronPasswordUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return UpdatePatronPasswordUseCase.Request.of(
            SecureString.of(requestObject.getOldPatronPassword()),
            SecureString.of(requestObject.getNewPatronPassword())
        );
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        char @NonNull [] oldPatronPassword;
        char @NonNull [] newPatronPassword;
    }
}
