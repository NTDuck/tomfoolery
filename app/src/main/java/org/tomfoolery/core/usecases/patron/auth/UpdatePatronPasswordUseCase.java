package org.tomfoolery.core.usecases.patron.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.PasswordEncoder;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.helpers.CredentialsVerifier;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public final class UpdatePatronPasswordUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<UpdatePatronPasswordUseCase.Request> {
    private final @NonNull PatronRepository patronRepository;
    private final @NonNull PasswordEncoder passwordEncoder;

    public static @NonNull UpdatePatronPasswordUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new UpdatePatronPasswordUseCase(authenticationTokenGenerator, authenticationTokenRepository, patronRepository, passwordEncoder);
    }

    private UpdatePatronPasswordUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.patronRepository = patronRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected @NonNull Collection<Class<? extends BaseUser>> getAllowedUserClasses() {
        return List.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException, PasswordInvalidException, PasswordMismatchException {
        val patronAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = getPatronFromAuthenticationToken(patronAuthenticationToken);

        val oldPatronPassword = request.getOldPatronPassword();
        ensurePasswordIsValid(oldPatronPassword);
        ensurePasswordsMatch(oldPatronPassword, patron);

        val newPatronPassword = request.getNewPatronPassword();
        ensurePasswordIsValid(newPatronPassword);

        val encodedNewPatronPassword = getEncodedNewPatronPassword(newPatronPassword);
        updatePatronPassword(patron, encodedNewPatronPassword);
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

    private static void ensurePasswordIsValid(@NonNull String password) throws PasswordInvalidException {
        if (!CredentialsVerifier.verifyPassword(password))
            throw new PasswordInvalidException();
    }

    private void ensurePasswordsMatch(@NonNull String patronPassword, @NonNull Patron patron) throws PasswordMismatchException {
        val patronCredentials = patron.getCredentials();
        val patronEncodedPassword = patronCredentials.getPassword();

        if (!this.passwordEncoder.verifyPassword(patronPassword, patronEncodedPassword))
            throw new PasswordMismatchException();
    }

    private @NonNull String getEncodedNewPatronPassword(@NonNull String patronNewPassword) {
        return this.passwordEncoder.encodePassword(patronNewPassword);
    }

    private void updatePatronPassword(@NonNull Patron patron, @NonNull String encodedNewPatronPassword) {
        val patronCredentials = patron.getCredentials();
        patronCredentials.setPassword(encodedNewPatronPassword);

        val patronAudit = patron.getAudit();
        val patronAuditTimestamps = patronAudit.getTimestamps();

        patronAuditTimestamps.setLastModified(Instant.now());
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String oldPatronPassword;
        @NonNull String newPatronPassword;
    }

    public static class PatronNotFoundException extends Exception {}
    public static class PasswordInvalidException extends Exception {}
    public static class PasswordMismatchException extends Exception {}
}