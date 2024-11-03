package org.tomfoolery.core.usecases.guest.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.security.PasswordEncoder;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.UserAndRepository;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.helpers.CredentialsVerifier;

import java.time.Instant;

@RequiredArgsConstructor(staticName = "of")
public final class LogUserInUseCase implements ThrowableFunction<LogUserInUseCase.Request<?>, LogUserInUseCase.Response> {
    private static final int TOKEN_LIFE_IN_MINUTES = 30;

    private final @NonNull UserRepositories userRepositories;

    private final @NonNull PasswordEncoder passwordEncoder;
    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator;
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository;

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
        saveAuthenticationToken(authenticationToken);

        return Response.of(authenticationToken);
    }

    private static <User extends BaseUser> void ensureUserCredentialsAreValid(User.@NonNull Credentials credentials) throws CredentialsInvalidException {
        if (!CredentialsVerifier.verifyCredentials(credentials))
            throw new CredentialsInvalidException();
    }

    private <User extends BaseUser> UserAndRepository<User> getUserAndRepositoryByUsername(@NonNull String username) throws UserNotFoundException {
        UserAndRepository<User> userAndRepository = this.userRepositories.getUserAndRepositoryByUsername(username);

        if (userAndRepository == null)
            throw new UserNotFoundException();

        return userAndRepository;
    }

    private <User extends BaseUser> void ensurePasswordsMatch(@NonNull String password, @NonNull User user) throws PasswordMismatchException {
        val credentials = user.getCredentials();
        val encodedPassword = credentials.getPassword();

        if (!this.passwordEncoder.verifyPassword(password, encodedPassword))
            throw new PasswordMismatchException();
    }

    private static <User extends BaseUser> void markUserAsLoggedIn(@NonNull User user) throws UserAlreadyLoggedInException {
        val userAudit = user.getAudit();

        if (userAudit.isLoggedIn())
            throw new UserAlreadyLoggedInException();

        userAudit.setLoggedIn(true);

        val userAuditTimestamps = userAudit.getTimestamps();
        userAuditTimestamps.setLastLogin(Instant.now());
    }

    private <User extends BaseUser> AuthenticationToken generateAuthenticationToken(@NonNull UserAndRepository<User> userAndRepository) {
        val user = userAndRepository.getUser();
        val userRepository = userAndRepository.getUserRepository();

        val userId = user.getId();
        val userClass = userRepository.getUserClass();
        val expiryTimestamp = Instant.now().plusSeconds(TOKEN_LIFE_IN_MINUTES * 60);

        return this.authenticationTokenGenerator.generateAuthenticationToken(userId, userClass, expiryTimestamp);
    }

    private void saveAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        this.authenticationTokenRepository.saveAuthenticationToken(authenticationToken);
    }

    @Value(staticConstructor = "of")
    public static class Request<User extends BaseUser> {
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
