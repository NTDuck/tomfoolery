package org.tomfoolery.core.usecases.external.user.auth;

import lombok.*;

import org.tomfoolery.core.dataproviders.UserRepository;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.utils.function.ThrowableFunction;

import java.util.Optional;
import java.util.SequencedCollection;

@RequiredArgsConstructor(staticName = "of")
public class LogUserInUseCase implements ThrowableFunction<LogUserInUseCase.Request, LogUserInUseCase.Response> {
    private final @NonNull SequencedCollection<UserRepository<?>> userRepositories;

    @Override
    public Response apply(@NonNull Request request)
    throws ValidationException, UserNotFoundException, UserAlreadyLoggedInException {
        val userCredentials = request.getUserCredentials();

        if (!isCredentialsValid(userCredentials))
            throw new ValidationException();

        ReadonlyUser user = this.userRepositories.stream()
                .map(userRepository -> )

        return Response.of(user);
    }

    private static boolean isCredentialsValid(@NonNull ReadonlyUser.Credentials userCredentials) {
        return isUsernameValid(userCredentials.getUsername())
            && isPasswordValid(userCredentials.getPassword());
    }

    private static boolean isUsernameValid(@NonNull String username) {
        return username.matches("^(?![0-9])(?!.*_$)[a-z0-9_]{8,16}$");
    }

    private static boolean isPasswordValid(@NonNull String password) {
        return true;
    }

    private static <User extends ReadonlyUser> @NonNull User findAndAuthenticateUserByCredentials(@NonNull UserRepository<User> userRepository, @NonNull User.Credentials userCredentials)
    throws UserNotFoundException, UserAlreadyLoggedInException {
        val user = Optional.ofNullable(userRepository.getByCredentials(userCredentials))
            .orElseThrow(UserNotFoundException::new);

        authenticateUser(user.get());
        userRepository.save(user);

        return user;
    }

    private static <User extends ReadonlyUser> void authenticateUser(@NonNull User user)
    throws UserAlreadyLoggedInException {
        val audit = user.getAudit();

        if (audit.isLoggedIn())
            throw new UserAlreadyLoggedInException();

        audit.setLoggedIn(true);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull ReadonlyUser.Credentials userCredentials;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull ReadonlyUser user;
    }

    public static class ValidationException extends Exception {}

    /**
     * UserNotFoundException and PasswordMismatchException are not differentiated,
     * as part of Security through Obscurity.
     */
    public static class UserNotFoundException extends Exception {}
    public static class UserAlreadyLoggedInException extends Exception {}
}
