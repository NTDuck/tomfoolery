package org.tomfoolery.infrastructures.adapters.controllers.external.patron.users.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.patron.users.persistence.DeletePatronAccountUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;

public final class DeletePatronAccountController implements ThrowableConsumer<DeletePatronAccountController.RequestObject> {
    private final @NonNull DeletePatronAccountUseCase deletePatronAccountUseCase;

    public static @NonNull DeletePatronAccountController of(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new DeletePatronAccountController(patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private DeletePatronAccountController(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.deletePatronAccountUseCase = DeletePatronAccountUseCase.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws DeletePatronAccountUseCase.AuthenticationTokenNotFoundException, DeletePatronAccountUseCase.AuthenticationTokenInvalidException, DeletePatronAccountUseCase.PatronNotFoundException, DeletePatronAccountUseCase.PasswordInvalidException, DeletePatronAccountUseCase.PasswordMismatchException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.deletePatronAccountUseCase.accept(requestModel);
    }

    private static DeletePatronAccountUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        val patronPassword = SecureString.of(requestObject.getPatronPassword());

        return DeletePatronAccountUseCase.Request.of(patronPassword);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        char @NonNull [] patronPassword;
    }
}
