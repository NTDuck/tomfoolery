package org.tomfoolery.core.usecases.interactive.user;

import lombok.*;

import org.tomfoolery.core.dataproviders.UserRepository;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.utils.function.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class LogUserInUseCase<User extends ReadonlyUser> implements ThrowableFunction<LogUserInUseCase<User>.Request, LogUserInUseCase<User>.Response> {
    private final @NonNull UserRepository<User> userRepository;

    @Override
    public Response apply(@NonNull Request request)
    throws ValidationException, UserNotFoundException, UserAlreadyLoggedInException {
        val userCredentials = request.getUserCredentials();

        if (!isCredentialsValid(userCredentials))
            throw new ValidationException();

        val user = this.userRepository.getByCredentials(userCredentials);
        if (user == null)
            throw new UserNotFoundException();

        val audit = user.getAudit();
        if (audit.isLoggedIn())
            throw new UserAlreadyLoggedInException();

        audit.setLoggedIn(true);
        this.userRepository.save(user);

        return Response.of(user);
    }

    private static <User extends ReadonlyUser> boolean isCredentialsValid(@NonNull User.Credentials userCredentials) {
        return true;
    }

    @Value(staticConstructor = "of")
    public class Request {
        @NonNull User.Credentials userCredentials;
    }

    @Value(staticConstructor = "of")
    public class Response {
        @NonNull User user;
    }

    public static class ValidationException extends Exception {}

    /**
     * The application does not differentiate between a "user not found" and a "password mismatch" exception,
     * as part of Security through Obscurity.
     * <p>
     * If the application differentiates between a "user not found" and a "password mismatch" exception,
     * an attacker can iterate through possible usernames to find which one exists.
     * They could then focus their attack on a specific username
     * by e.g. trying out all possible password combinations.
     */
    public static class UserNotFoundException extends Exception {}
    public static class UserAlreadyLoggedInException extends Exception {}
}
