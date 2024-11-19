package org.tomfoolery.core.usecases.patron.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;
import org.tomfoolery.core.utils.helpers.auth.security.CredentialsVerifier;

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
        val encodedNewPatronPassword = this.passwordEncoder.encodePassword(rawNewPatronPassword);

        this.updatePatronPassword(patron, encodedNewPatronPassword);

        this.patronRepository.save(patron);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken staffAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(staffAuthenticationToken);

        if (patronId == null)
            throw new AuthenticationTokenInvalidException();

        val patron = this.patronRepository.getById(patronId);

        if (patron == null)
            throw new PatronNotFoundException();

        return patron;
    }

    private void ensurePasswordIsValid(@NonNull SecureString rawPassword) throws PasswordInvalidException {
        if (!CredentialsVerifier.verifyPassword(rawPassword))
            throw new PasswordInvalidException();
    }

    private void ensurePasswordsMatch(@NonNull SecureString rawPatronPassword, @NonNull Patron patron) throws PasswordMismatchException {
        val encodedPatronCredentials = patron.getCredentials();
        val encodedPatronPassword = encodedPatronCredentials.getPassword();

        if (!this.passwordEncoder.verifyPassword(rawPatronPassword, encodedPatronPassword))
            throw new PasswordMismatchException();
    }

    private void updatePatronPassword(@NonNull Patron patron, @NonNull SecureString encodedNewPatronPassword) {
        val oldEncodedPatronCredentials = patron.getCredentials();
        oldEncodedPatronCredentials.setPassword(encodedNewPatronPassword);

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
