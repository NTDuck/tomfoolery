package org.tomfoolery.core.usecases.guest.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;
import org.tomfoolery.core.utils.dataclasses.common.UserAndRepository;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.helpers.auth.security.CredentialsVerifier;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor(staticName = "of")
public final class LogUserInUseCase implements ThrowableFunction<LogUserInUseCase.Request, LogUserInUseCase.Response> {
    private static final @NonNull Duration AUTHENTICATION_TOKEN_LIFESPAN = Duration.ofHours(6);

    private final @NonNull UserRepositories userRepositories;

    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator;
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository;
    private final @NonNull PasswordEncoder passwordEncoder;

    @Override
    public @NonNull Response apply(@NonNull Request request) throws CredentialsInvalidException, UserNotFoundException, PasswordMismatchException {
        val rawUserCredentials = request.getRawUserCredentials();
        val username = rawUserCredentials.getUsername();
        val rawPassword = rawUserCredentials.getPassword();

        this.ensureUserCredentialsAreValid(rawUserCredentials);

        val userAndRepository = this.getUserAndRepositoryByUsername(username);
        val userRepository = userAndRepository.getUserRepository();
        val user = userAndRepository.getUser();

        this.ensurePasswordsMatch(rawPassword, user);

        this.markUserAsLoggedIn(user);
        userRepository.save(user);

        val authenticationToken = this.generateAuthenticationToken(user);
        this.saveAuthenticationTokenToRepository(authenticationToken);

        val userClass = user.getClass();
        return Response.of(userClass);
    }

    private <User extends BaseUser> void ensureUserCredentialsAreValid(User.@NonNull Credentials rawUserCredentials) throws CredentialsInvalidException {
        if (!CredentialsVerifier.verifyCredentials(rawUserCredentials))
            throw new CredentialsInvalidException();
    }

    private <User extends BaseUser> UserAndRepository<User> getUserAndRepositoryByUsername(@NonNull String username) throws UserNotFoundException {
        UserAndRepository<User> userAndRepository = this.userRepositories.getUserAndRepositoryByUsername(username);

        if (userAndRepository == null)
            throw new UserNotFoundException();

        return userAndRepository;
    }

    private <User extends BaseUser> void ensurePasswordsMatch(@NonNull SecureString rawPassword, @NonNull User user) throws PasswordMismatchException {
        val credentials = user.getCredentials();
        val encodedPassword = credentials.getPassword();

        if (!this.passwordEncoder.verifyPassword(rawPassword, encodedPassword))
            throw new PasswordMismatchException();
    }

    private <User extends BaseUser> void markUserAsLoggedIn(@NonNull User user) {
        val userAuditTimestamps = user.getAudit().getTimestamps();
        userAuditTimestamps.setLastLogin(Instant.now());
    }

    private <User extends BaseUser> AuthenticationToken generateAuthenticationToken(@NonNull User user) {
        val userId = user.getId();
        val userClass = user.getClass();
        val expiryTimestamp = Instant.now().plus(AUTHENTICATION_TOKEN_LIFESPAN);

        return this.authenticationTokenGenerator.generateAuthenticationToken(userId, userClass, expiryTimestamp);
    }

    private void saveAuthenticationTokenToRepository(@NonNull AuthenticationToken authenticationToken) {
        this.authenticationTokenRepository.saveAuthenticationToken(authenticationToken);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        BaseUser.@NonNull Credentials rawUserCredentials;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Class<? extends BaseUser> loggedInUserClass;
    }

    public static class CredentialsInvalidException extends Exception {}
    public static class UserNotFoundException extends Exception {}
    public static class PasswordMismatchException extends Exception {}
}
