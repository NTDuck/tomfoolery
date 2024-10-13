package org.tomfoolery.core.usecases.external.user.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.PasswordEncoder;
import org.tomfoolery.core.dataproviders.UserRepositories;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.utils.function.ThrowableFunction;
import org.tomfoolery.core.utils.validators.CredentialsValidator;

import java.time.LocalDateTime;

@RequiredArgsConstructor(staticName = "of")
public class LogUserInUseCase implements ThrowableFunction<LogUserInUseCase.Request<?>, LogUserInUseCase.Response<?>> {
    private final @NonNull UserRepositories userRepositories;
    private final @NonNull PasswordEncoder passwordEncoder;

    @Override
    public @NonNull Response<?> apply(@NonNull Request<?> request)
    throws ValidationException, UserNotFoundException, PasswordMismatchException, UserAlreadyLoggedInException {
        val userCredentials = request.getUserCredentials();
        if (!CredentialsValidator.isCredentialsValid(userCredentials))
            throw new ValidationException();

        val username = userCredentials.getUsername();
        val userRepository = this.userRepositories.getUserRepositoryByUsername(username);
        if (userRepository == null)
            throw new UserNotFoundException();

        val user = userRepository.getByUsername(username);
        assert user != null;

        val password = userCredentials.getPassword();
        val encodedUserCredentials = user.getCredentials();
        val encodedPassword = encodedUserCredentials.getPassword();
        if (!this.passwordEncoder.verify(password, encodedPassword))
            throw new PasswordMismatchException();

        markUserAsLoggedIn(user);

        userRepository.save(user);
        return Response.of(user);
    }

    private static <User extends ReadonlyUser> void markUserAsLoggedIn(@NonNull User user)
    throws UserAlreadyLoggedInException {
        val audit = user.getAudit();

        if (audit.isLoggedIn())
            throw new UserAlreadyLoggedInException();

        audit.setLoggedIn(true);

        val timestamps = audit.getTimestamps();
        timestamps.setLastLogin(LocalDateTime.now());
    }

    @Value(staticConstructor = "of")
    public static class Request<User extends ReadonlyUser> {
        User.@NonNull Credentials userCredentials;
    }

    @Value(staticConstructor = "of")
    public static class Response<User extends ReadonlyUser> {
        @NonNull User user;
    }

    public static class ValidationException extends Exception {}
    public static class UserNotFoundException extends Exception {}
    public static class PasswordMismatchException extends Exception {}
    public static class UserAlreadyLoggedInException extends Exception {}
}
