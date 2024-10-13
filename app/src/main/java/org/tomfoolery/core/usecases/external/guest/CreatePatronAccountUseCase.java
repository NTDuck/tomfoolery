package org.tomfoolery.core.usecases.external.guest;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.PasswordEncoder;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.domain.Patron;
import org.tomfoolery.core.utils.function.ThrowableFunction;
import org.tomfoolery.core.utils.validators.CredentialsValidator;

@RequiredArgsConstructor(staticName = "of")
public class CreatePatronAccountUseCase implements ThrowableFunction<CreatePatronAccountUseCase.Request, CreatePatronAccountUseCase.Response> {
    private final @NonNull PatronRepository patronRepository;
    private final @NonNull PasswordEncoder passwordEncoder;

    @Override
    public @NonNull Response apply(@NonNull Request request) throws ValidationException, PatronAlreadyExistsException {
        val patronCredentials = request.getPatronCredentials();
        if (!CredentialsValidator.isCredentialsValid(patronCredentials))
            throw new ValidationException();

        val patronUsername = patronCredentials.getUsername();
        if (this.patronRepository.contains(patronUsername))
            throw new PatronAlreadyExistsException();

        this.passwordEncoder.encode(patronCredentials);

        val patronAudit = Patron.Audit.of();
        val patronMetadata = request.getPatronMetadata();

        val patron = Patron.of(patronCredentials, patronAudit, patronMetadata);
        this.patronRepository.save(patron);

        return Response.of(patron);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Patron.@NonNull Credentials patronCredentials;
        Patron.@NonNull Metadata patronMetadata;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Patron patron;
    }

    public static class ValidationException extends Exception {}
    public static class PatronAlreadyExistsException extends Exception {}
}
