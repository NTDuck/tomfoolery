package org.tomfoolery.infrastructures.adapters.controllers.patron.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.patron.users.persistence.DeletePatronAccountUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

public final class DeletePatronAccountController implements ThrowableConsumer<DeletePatronAccountController.RequestObject> {
    private final @NonNull DeletePatronAccountUseCase deletePatronAccountUseCase;

    public static @NonNull DeletePatronAccountController of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new DeletePatronAccountController(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private DeletePatronAccountController(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.deletePatronAccountUseCase = DeletePatronAccountUseCase.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws DeletePatronAccountUseCase.AuthenticationTokenNotFoundException, DeletePatronAccountUseCase.AuthenticationTokenInvalidException, DeletePatronAccountUseCase.PatronNotFoundException, DeletePatronAccountUseCase.PasswordInvalidException, DeletePatronAccountUseCase.PasswordMismatchException {
        val requestModel = requestObject.toRequestModel();
        this.deletePatronAccountUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        char @NonNull [] patronPassword;

        private DeletePatronAccountUseCase.@NonNull Request toRequestModel() {
            val patronPassword = SecureString.of(this.patronPassword);

            return DeletePatronAccountUseCase.Request.of(patronPassword);
        }
    }
}
