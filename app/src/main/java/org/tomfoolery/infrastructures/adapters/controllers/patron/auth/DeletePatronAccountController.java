package org.tomfoolery.infrastructures.adapters.controllers.patron.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.patron.auth.DeletePatronAccountUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class DeletePatronAccountController implements ThrowableConsumer<DeletePatronAccountController.RequestObject> {
    private final @NonNull DeletePatronAccountUseCase deletePatronAccountUseCase;

    public static @NonNull DeletePatronAccountController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new DeletePatronAccountController(authenticationTokenGenerator, authenticationTokenRepository, patronRepository, passwordEncoder);
    }

    private DeletePatronAccountController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.deletePatronAccountUseCase = DeletePatronAccountUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, patronRepository, passwordEncoder);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws DeletePatronAccountUseCase.AuthenticationTokenNotFoundException, DeletePatronAccountUseCase.AuthenticationTokenInvalidException, DeletePatronAccountUseCase.PatronNotFoundException, DeletePatronAccountUseCase.PasswordInvalidException, DeletePatronAccountUseCase.PasswordMismatchException {
        val requestModel = requestObject.toRequestModel();
        this.deletePatronAccountUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String patronPassword;

        private DeletePatronAccountUseCase.@NonNull Request toRequestModel() {
            return DeletePatronAccountUseCase.Request.of(patronPassword);
        }
    }
}
