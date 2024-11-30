package org.tomfoolery.core.usecases.users.account.patron.modification;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;
import org.tomfoolery.core.utils.helpers.verifiers.auth.security.PasswordVerifier;

import java.time.Instant;
import java.util.Set;

public final class UpdatePatronPasswordUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<UpdatePatronPasswordUseCase.Request> {
    private final @NonNull PatronRepository patronRepository;
    private final @NonNull PasswordEncoder passwordEncoder;

    public static @NonNull UpdatePatronPasswordUseCase of(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new UpdatePatronPasswordUseCase(patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private UpdatePatronPasswordUseCase(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.patronRepository = patronRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException, PasswordInvalidException, PasswordMismatchException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = this.getPatronFromAuthenticationToken(patronAuthenticationToken);

        val rawOldPatronPassword = request.getRawOldPatronPassword();
        this.ensurePasswordIsValid(rawOldPatronPassword);
        this.ensurePasswordsMatch(rawOldPatronPassword, patron);

        val rawNewPatronPassword = request.getRawNewPatronPassword();
        this.ensurePasswordIsValid(rawNewPatronPassword);
        val encodedNewPatronPassword = this.passwordEncoder.encode(rawNewPatronPassword);

        this.updatePatronPassword(patron, encodedNewPatronPassword);

        this.patronRepository.save(patron);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken patronAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.getUserIdFromAuthenticationToken(patronAuthenticationToken);
        val patron = this.patronRepository.getById(patronId);

        if (patron == null)
            throw new PatronNotFoundException();

        return patron;
    }

    private void ensurePasswordIsValid(@NonNull SecureString rawPassword) throws PasswordInvalidException {
        if (!PasswordVerifier.verify(rawPassword))
            throw new PasswordInvalidException();
    }

    private void ensurePasswordsMatch(@NonNull SecureString rawPatronPassword, @NonNull Patron patron) throws PasswordMismatchException {
        val encodedPatronCredentials = patron.getCredentials();
        val encodedPatronPassword = encodedPatronCredentials.getPassword();

        if (!this.passwordEncoder.verify(rawPatronPassword, encodedPatronPassword))
            throw new PasswordMismatchException();
    }

    private void updatePatronPassword(@NonNull Patron patron, @NonNull SecureString encodedNewPatronPassword) {
        val encodedOldPatronCredentials = patron.getCredentials();
        val encodedNewPatronCredentials = encodedOldPatronCredentials.withPassword(encodedNewPatronPassword);
        patron.setCredentials(encodedNewPatronCredentials);

        val patronAuditTimestamps = patron.getAudit().getTimestamps();
        patronAuditTimestamps.setLastModified(Instant.now());
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull SecureString rawOldPatronPassword;
        @NonNull SecureString rawNewPatronPassword;
    }

    public static class PatronNotFoundException extends Exception {}
    public static class PasswordInvalidException extends Exception {}
    public static class PasswordMismatchException extends Exception {}
}
