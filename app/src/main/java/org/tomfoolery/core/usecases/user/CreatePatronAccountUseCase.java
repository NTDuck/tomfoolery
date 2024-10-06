package org.tomfoolery.core.usecases.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.domain.Patron;
import org.tomfoolery.core.utils.function.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class CreatePatronAccountUseCase {
    private final @NonNull PatronRepository patrons;

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull Patron.Credentials credentials;
        @NonNull Patron.Metadata metadata;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Patron patron;
    }

    public static class PatronAlreadyExistsException extends Exception {}
    public static class PatronValidationException extends Exception {}
}
