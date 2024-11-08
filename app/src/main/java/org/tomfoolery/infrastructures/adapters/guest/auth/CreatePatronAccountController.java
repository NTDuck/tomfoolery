package org.tomfoolery.infrastructures.adapters.guest.auth;

import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.usecases.guest.auth.CreatePatronAccountUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.infrastructures.utils.helpers.reflection.Cloner;

public final class CreatePatronAccountController implements ThrowableConsumer<CreatePatronAccountController.RequestObject> {
    private final @NonNull CreatePatronAccountUseCase createPatronAccountUseCase;

    public static @NonNull CreatePatronAccountController of(@NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new CreatePatronAccountController(patronRepository, passwordEncoder);
    }

    private CreatePatronAccountController(@NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.createPatronAccountUseCase = CreatePatronAccountUseCase.of(patronRepository, passwordEncoder);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws CreatePatronAccountUseCase.PatronCredentialsInvalidException, CreatePatronAccountUseCase.PatronAlreadyExistsException {
        val requestModel = generateRequestModelFromRequestObject(requestObject);
        this.createPatronAccountUseCase.accept(requestModel);
    }

    @SneakyThrows
    private static CreatePatronAccountUseCase.@NonNull Request generateRequestModelFromRequestObject(@NonNull RequestObject requestObject) {
        return Cloner.cloneFrom(requestObject, CreatePatronAccountUseCase.Request.class);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        Patron.@NonNull Credentials patronCredentials;
        Patron.@NonNull Metadata patronMetadata;
    }
}
