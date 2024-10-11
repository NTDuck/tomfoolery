package org.tomfoolery.core.usecases.external.user.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.UserRepositories;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.utils.function.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class LogUserInUseCase implements ThrowableFunction<LogUserInUseCase.Request, LogUserInUseCase.Response<?>> {
    private final @NonNull UserRepositories userRepositories;

    @Override
    public @NonNull Response<?> apply(@NonNull Request request)
    throws ValidationException, UserNotFoundException, UserAlreadyLoggedInException {
        val userCredentials = request.getUserCredentials();

        if (!isCredentialsValid(userCredentials))
            throw new ValidationException();

        val userRepository = this.userRepositories.getUserRepositoryByUserCredentials(userCredentials);

        if (userRepository == null)
            throw new UserNotFoundException();

        val user = userRepository.getByCredentials(userCredentials);
        assert user != null;

        markUserAsLoggedIn(user);

        userRepository.save(user);
        return Response.of(user);
    }

    private static boolean isCredentialsValid(ReadonlyUser.@NonNull Credentials userCredentials) {
        return isUsernameValid(userCredentials.getUsername())
            && isPasswordValid(userCredentials.getPassword());
    }

    private static boolean isUsernameValid(@NonNull String username) {
        return username.matches("^(?![0-9])(?!.*_$)[a-z0-9_]{8,16}$");
    }

    private static boolean isPasswordValid(@NonNull String password) {
        return true;
    }

    private static <User extends ReadonlyUser> void markUserAsLoggedIn(@NonNull User user)
    throws UserAlreadyLoggedInException {
        val audit = user.getAudit();

        if (audit.isLoggedIn())
            throw new UserAlreadyLoggedInException();

        audit.setLoggedIn(true);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        ReadonlyUser.@NonNull Credentials userCredentials;
    }

    @Value(staticConstructor = "of")
    public static class Response<User extends ReadonlyUser> {
        @NonNull User user;
    }

    public static class ValidationException extends Exception {}

    /**
     * UserNotFoundException and PasswordMismatchException are not differentiated,
     * as part of Security through Obscurity.
     */
    public static class UserNotFoundException extends Exception {}
    public static class UserAlreadyLoggedInException extends Exception {}
}
