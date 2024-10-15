package org.tomfoolery.core.usecases.external.user.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.core.dataproviders.UserRepositories;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.domain.auth.AuthenticationToken;
import org.tomfoolery.core.utils.services.CredentialsVerificationService;
import org.tomfoolery.core.utils.structs.UserAndRepository;
import org.tomfoolery.core.utils.functional.ThrowableFunction;

import java.time.LocalDateTime;

@RequiredArgsConstructor(staticName = "of")
public class LogUserInUseCase implements ThrowableFunction<LogUserInUseCase.Request<?>, LogUserInUseCase.Response> {
    private static final int TOKEN_LIFE_IN_MINUTES = 30;

    private final @NonNull UserRepositories userRepositories;

    private final @NonNull PasswordService passwordService;
    private final @NonNull AuthenticationTokenService authenticationTokenService;

    @Override
    public @NonNull Response apply(@NonNull Request<?> request) throws CredentialsInvalidException, UserNotFoundException, PasswordMismatchException, UserAlreadyLoggedInException {
        val userCredentials = request.getUserCredentials();
        val username = userCredentials.getUsername();
        val password = userCredentials.getPassword();

        ensureUserCredentialsAreValid(userCredentials);

        val userAndRepository = getUserAndRepositoryByUsername(username);
        val userRepository = userAndRepository.getUserRepository();
        val user = userAndRepository.getUser();

        ensurePasswordsMatch(password, user);

        markUserAsLoggedIn(user);
        userRepository.save(user);

        val authenticationToken = generateAuthenticationToken(userAndRepository);
        return Response.of(authenticationToken);
    }

    private static <User extends ReadonlyUser> void ensureUserCredentialsAreValid(User.@NonNull Credentials credentials) throws CredentialsInvalidException {
        if (!CredentialsVerificationService.verifyCredentials(credentials))
            throw new CredentialsInvalidException();
    }

    private <User extends ReadonlyUser> UserAndRepository<User> getUserAndRepositoryByUsername(@NonNull String username) throws UserNotFoundException {
        UserAndRepository<User> userAndRepository = this.userRepositories.getUserAndRepositoryByUsername(username);

        if (userAndRepository == null)
            throw new UserNotFoundException();

        return userAndRepository;
    }

    private <User extends ReadonlyUser> void ensurePasswordsMatch(@NonNull String password, @NonNull User user) throws PasswordMismatchException {
        val credentials = user.getCredentials();
        val encodedPassword = credentials.getPassword();

        if (!this.passwordService.verifyPassword(password, encodedPassword))
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

    private <User extends ReadonlyUser> AuthenticationToken generateAuthenticationToken(@NonNull UserAndRepository<User> userAndRepository) {
        val user = userAndRepository.getUser();
        val userRepository = userAndRepository.getUserRepository();

        val userId = user.getId();
        val userClass = userRepository.getUserClass();
        val expiryTimestamp = LocalDateTime.now().plusMinutes(TOKEN_LIFE_IN_MINUTES);

        return this.authenticationTokenService.generateToken(userId, userClass, expiryTimestamp);
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
