package org.tomfoolery.core.usecases.external.user.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.PasswordEncoder;
import org.tomfoolery.core.dataproviders.UserRepositories;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.domain.auth.AuthenticationToken;
import org.tomfoolery.core.usecases.utils.services.CredentialsVerifier;
import org.tomfoolery.core.usecases.utils.structs.UserAndRepository;
import org.tomfoolery.core.utils.function.ThrowableFunction;

import java.time.LocalDateTime;

@RequiredArgsConstructor(staticName = "of")
public class LogUserInUseCase implements ThrowableFunction<LogUserInUseCase.Request<?>, LogUserInUseCase.Response> {
    private final @NonNull UserRepositories userRepositories;

    private final @NonNull PasswordEncoder passwordEncoder;
    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator;

    @Override
    public @NonNull Response apply(@NonNull Request<?> request) throws CredentialsInvalidException, UserNotFoundException, PasswordMismatchException, UserAlreadyLoggedInException {
        val userCredentials = request.getUserCredentials();
        val username = userCredentials.getUsername();
        val password = userCredentials.getPassword();

        verifyUserCredentials(userCredentials);

        val userAndRepository = this.getUserAndRepositoryByUsername(username);
        val userRepository = userAndRepository.getUserRepository();
        val user = userAndRepository.getUser();

        this.verifyUserPassword(password, user);

        markUserAsLoggedIn(user);
        userRepository.save(user);

        val authenticationToken = this.generateAuthenticationToken(user);
        return Response.of(authenticationToken);
    }

    private static <User extends ReadonlyUser> void verifyUserCredentials(User.@NonNull Credentials credentials) throws CredentialsInvalidException {
        if (!CredentialsVerifier.verifyCredentials(credentials))
            throw new CredentialsInvalidException();
    }

    private <User extends ReadonlyUser> UserAndRepository<User> getUserAndRepositoryByUsername(@NonNull String username) throws UserNotFoundException {
        UserAndRepository<User> userAndRepository = this.userRepositories.getUserAndRepositoryByUsername(username);

        if (userAndRepository == null)
            throw new UserNotFoundException();

        return userAndRepository;
    }

    private <User extends ReadonlyUser> void verifyUserPassword(@NonNull String password, @NonNull User user) throws PasswordMismatchException {
        val credentials = user.getCredentials();
        val encodedPassword = credentials.getPassword();

        if (!this.passwordEncoder.verify(password, encodedPassword))
            throw new PasswordMismatchException();
    }

    private static <User extends ReadonlyUser> void markUserAsLoggedIn(@NonNull User user) throws UserAlreadyLoggedInException {
        val audit = user.getAudit();

        if (audit.isLoggedIn())
            throw new UserAlreadyLoggedInException();

        audit.setLoggedIn(true);

        val timestamps = audit.getTimestamps();
        timestamps.setLastLogin(LocalDateTime.now());
    }

    private <User extends ReadonlyUser> AuthenticationToken generateAuthenticationToken(@NonNull User user) {
        val userId = user.getId();
        return this.authenticationTokenGenerator.generateToken(userId);
    }

    @Value(staticConstructor = "of")
    public static class Request<User extends ReadonlyUser> {
        User.@NonNull Credentials userCredentials;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull AuthenticationToken authenticationToken;
    }

    public static class CredentialsInvalidException extends Exception {}
    public static class UserNotFoundException extends Exception {}
    public static class PasswordMismatchException extends Exception {}
    public static class UserAlreadyLoggedInException extends Exception {}
}
